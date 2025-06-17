#!/usr/bin/env bash
set -euo pipefail

#############################################
# Usage & arg parsing
#############################################
usage() {
  echo "Usage: $0 --app-name <name> --env <dev|test|prod>"
  exit 1
}

APP_NAME=""
ENV=""
REGION="us-east-1"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --app-name) APP_NAME="$2"; shift 2 ;;
    --env)      ENV="$2"; shift 2 ;;
    -h|--help)  usage ;;
    *) echo "Unknown option $1"; usage ;;
  esac
done

[[ -z "$APP_NAME" || -z "$ENV" ]] && usage
if [[ ! "$ENV" =~ ^(dev|test|prod)$ ]]; then
  echo "ENV must be dev, test or prod"; exit 1; fi

#############################################
# Derived names
#############################################
BUCKET="${APP_NAME}-artifacts-${ENV}"
STACK_NAME="${APP_NAME}-sam-${ENV}"
ECR_REPO="${APP_NAME}-sam"
IMAGE_URI=""
PARAM_OVERRIDES="Env=${ENV}"
IMAGE_REPO_FLAG=""

echo "▶ APP_NAME      = $APP_NAME"
echo "▶ ENV           = $ENV"
echo "▶ STACK_NAME    = $STACK_NAME"
echo "▶ BUCKET        = $BUCKET"
echo "▶ REGION        = $REGION"
echo "▶ ECR_REPO      = $ECR_REPO"
echo "-----------------------------------------"

#############################################
# Pre-checks for required CLIs
#############################################
for cmd in aws sam.cmd docker mvn; do
  command -v $cmd >/dev/null || { echo "❌ $cmd not installed"; exit 1; }
done

#############################################
# Check and clean up failed stacks
#############################################
check_and_cleanup_stack() {
  local stack_status
  stack_status=$(aws cloudformation describe-stacks --stack-name "$STACK_NAME" --region "$REGION" --query 'Stacks[0].StackStatus' --output text 2>/dev/null || echo "STACK_NOT_FOUND")
  
  if [[ "$stack_status" == "ROLLBACK_COMPLETE" ]]; then
    echo "🧹 Found stack in ROLLBACK_COMPLETE state. Cleaning up..."
    aws cloudformation delete-stack --stack-name "$STACK_NAME" --region "$REGION"
    echo "⏳ Waiting for stack deletion to complete..."
    aws cloudformation wait stack-delete-complete --stack-name "$STACK_NAME" --region "$REGION"
    echo "✅ Stack cleanup completed"
  elif [[ "$stack_status" != "STACK_NOT_FOUND" ]]; then
    echo "ℹ️ Stack exists with status: $stack_status"
  fi
}

#############################################
# Maven build (jar + dependency folder)
#############################################
echo "🛠  Building Maven project"
mvn -q clean package \
    dependency:copy-dependencies -DoutputDirectory=target/dependency

#############################################
# Ensure bucket exists
#############################################
if ! aws s3 ls "s3://$BUCKET" --region "$REGION" >/dev/null 2>&1; then
  echo "🪣 Creating S3 bucket $BUCKET"
  aws s3 mb "s3://$BUCKET" --region "$REGION"
fi

#############################################
# Ensure ECR repo exists & get URI
#############################################
ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
IMAGE_URI="${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com/${ECR_REPO}"

if ! aws ecr describe-repositories --repository-names "$ECR_REPO" \
        --region "$REGION" >/dev/null 2>&1; then
  echo "📦 Creating ECR repo $ECR_REPO"
  aws ecr create-repository --repository-name "$ECR_REPO" \
      --region "$REGION" --image-scanning-configuration scanOnPush=true >/dev/null
fi

echo "🔐 Logging in to ECR"
aws ecr get-login-password --region "$REGION" | \
  docker login --username AWS --password-stdin "${ACCOUNT_ID}.dkr.ecr.${REGION}.amazonaws.com"

#############################################
# Check and clean up failed stacks
#############################################
check_and_cleanup_stack

#############################################
# sam.cmd build (container mode)
#############################################
echo "🧹 Cleaning up previous build"
rm -rf .aws-sam/build 2>/dev/null || true
echo "🐳 sam.cmd build --use-container"
sam.cmd build --use-container

#############################################
# sam.cmd deploy (non-interactive)
#############################################
echo "🚀 Deploying stack $STACK_NAME"
sam.cmd deploy \
  --stack-name "$STACK_NAME" \
  --region "$REGION" \
  --s3-bucket "$BUCKET" \
  --image-repository "$IMAGE_URI" \
  --capabilities CAPABILITY_IAM \
  --parameter-overrides "$PARAM_OVERRIDES" \
  --no-confirm-changeset \
  --no-fail-on-empty-changeset

#############################################
# Persist samconfig.toml for next runs
#############################################
cat > samconfig.toml <<EOF
version = 0.1

[default.deploy]
stack_name = "$STACK_NAME"
region = "$REGION"
s3_bucket = "$BUCKET"
image_repositories = ["PlantUMLFunction=$IMAGE_URI"]
capabilities = "CAPABILITY_IAM"
parameter_overrides = "$PARAM_OVERRIDES"
EOF

echo "✅ Deployment finished. Configuration saved to samconfig.toml"
