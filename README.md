# Render PlantUML diagrams with AWS Lambda

A serverless UI + API to render [PlantUML](http://plantuml.com) diagrams using a single Lambda function.

## Features

- Single Lambda function architecture for better performance and cost efficiency
- Drop-in replacement for the official PlantUML server
- Supports PNG, SVG, and TXT rendering
- Enhanced logging system with request tracking
- CORS support for cross-origin requests
- Custom domain support (optional)

## Drop in replacement for official PlantUML server

This can be used as a drop in replacement for scenarios
where http://www.plantuml.com/plantuml is used as a rendering endpoint. You can avoid sending the diagram source to a server outside your control and use an encrypted HTTPS endpoint for the diagram traffic.

This doesn't support everything the official PlantUML server does but should be good for most intents and purposes (PNG, SVG and TXT rendering).

For example, to have Visual Studio Code PlantUML plugin render using your own serverless deployment, set the following properties in vscode for the plugin: 

```json
    "plantuml.render": "PlantUMLServer",
    "plantuml.server": "https://your-endpoint-here"
```

Use `"plantuml.server": "https://plantuml.nitorio.us"` if you'd like to try before you deploy your own.

## API Endpoints

The single Lambda function handles all requests through different paths:

- `/` - UI interface
- `/png/{encoded}` - PNG diagram rendering
- `/svg/{encoded}` - SVG diagram rendering
- `/txt/{encoded}` - Text diagram rendering
- `/map/{encoded}` - Map diagram rendering
- `/check` - Syntax checking

## Demo

### PNG Diagram

![Rendered PNG diagram should be here](https://plantuml.nitorio.us/png/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

### SVG Diagram 

![Rendered SVG diagram should be here](https://plantuml.nitorio.us/svg/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

### TXT Diagram

https://plantuml.nitorio.us/txt/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0

## Build

```bash
# Install dependencies
npm ci

# Build the project
mvn clean package
```

## Deploy

You can deploy with Serverless framework or AWS SAM.

### Serverless framework:

1. Edit `serverless.yml` to replace `custom.domains.dev` and `custom.domains.prod` with your own domain names.
   - If you don't want a custom domain name, remove or comment out the `serverless-domain-manager` plugin from the plugins list and skip the `sls create-cert` and `sls create_domain` commands.

2. Create ACM certificate:
```bash
sls create-cert
```

3. Create API Gateway custom domain:
```bash
sls create_domain
```

4. Deploy:
```bash
sls deploy
```

The above steps deploy the default `dev` stage. To deploy the `prod` stage, add `--stage prod` to each command.

### AWS SAM

1. Create an ECR repository:
```bash
aws ecr create-repository --repository-name plantuml-sam \
--image-tag-mutability IMMUTABLE --image-scanning-configuration scanOnPush=true
```

2. Deploy using the provided script:
```bash
./sam-deploy.sh -b <your-s3-bucket>
```

The SAM deployment doesn't include custom domains currently.

## Request Tracking

The API supports request tracking through query parameters:
- `currentRequestNumber`: Current request number
- `callingRequestNumber`: Calling request number

Example:
```
https://your-endpoint/png/encoded-diagram?currentRequestNumber=1&callingRequestNumber=2
```

## Contributing

Feel free to submit issues and enhancement requests!
