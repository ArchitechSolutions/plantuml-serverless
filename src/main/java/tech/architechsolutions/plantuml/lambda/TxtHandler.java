package tech.architechsolutions.plantuml.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import tech.architechsolutions.plantuml.PlantUmlUtil;
import tech.architechsolutions.plantuml.lambda.exception.StatusCodeException;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Collections;

public class TxtHandler extends LambdaBase implements RequestStreamHandler {

    private static final String TYPE_IDENTIFIER = "txt";
    private final PlantUmlUtil plantUmlUtil = new PlantUmlUtil();

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        JSONObject event = parseEvent(inputStream);
        String encodedUml = getEncodedUml(event);
        final String etag = plantUmlUtil.getEtag(encodedUml, TYPE_IDENTIFIER);
        if (isMatchingEtag(event, etag)) {
            send304Response(outputStream, Collections.emptyMap());
            return;
        }
        try {
            SourceStringReader reader = plantUmlUtil.readDiagram(encodedUml);
            ByteArrayOutputStream baos = plantUmlUtil.renderDiagram(reader, DiagramType.TEXT_PLAIN);
            byte[] bytes = baos.toByteArray();
            String base64Response = Base64.getEncoder().encodeToString(bytes);
            SyntaxCheckResult syntaxCheckResult = plantUmlUtil.checkSyntax(encodedUml);
            if (!syntaxCheckResult.isError()) {
                sendOKDiagramResponse(outputStream, base64Response, DiagramType.TEXT_PLAIN,
                        getCacheHeaders(etag, DEFAULT_MAX_AGE));
            } else {
                sendDiagramResponse(outputStream, base64Response, DiagramType.TEXT_PLAIN,
                        String.valueOf(HttpStatus.SC_UNPROCESSABLE_ENTITY));
            }
        } catch (StatusCodeException sce) {
            sendExceptionResponse(outputStream, sce);
        }
    }

}