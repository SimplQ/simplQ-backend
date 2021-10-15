package me.simplq.controller.converters;

import me.simplq.controller.model.queue.QueueEventsCsvResponse;
import me.simplq.controller.model.queue.QueueEventsResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Component
public class QueueEventsCsvResponseConverter extends AbstractHttpMessageConverter<QueueEventsCsvResponse> {

    private static final MediaType TEXT_CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    QueueEventsCsvResponseConverter() {
        super(TEXT_CSV);
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return QueueEventsCsvResponse.class.equals(aClass);
    }

    @Override
    protected QueueEventsCsvResponse readInternal(Class<? extends QueueEventsCsvResponse> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        throw new HttpMessageNotReadableException("Reading history from CSV not supported", httpInputMessage);
    }

    @Override
    protected void writeInternal(QueueEventsCsvResponse response, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {
        output.getHeaders().setContentType(TEXT_CSV);
        output.getHeaders().setContentDisposition(ContentDisposition.builder("attachment").filename(response.getFileName()).build());
        CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(output.getBody()), CSVFormat.DEFAULT);
        for (QueueEventsResponse.Event event : response.getEventResponse().getEvents()) {
            printEvent(csvPrinter, event);
        }
        csvPrinter.flush();
        csvPrinter.close();
    }

    private void printEvent(CSVPrinter csvPrinter, QueueEventsResponse.Event event) throws IOException {
        csvPrinter.printRecord(
                event.getTokenNumber(),
                event.getTokenName(),
                event.getEventType(),
                event.getEventTimestamp()
        );
    }
}
