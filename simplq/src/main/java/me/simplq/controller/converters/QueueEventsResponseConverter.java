package me.simplq.controller.converters;

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
public class QueueEventsResponseConverter extends AbstractHttpMessageConverter<QueueEventsResponse> {

    private static final MediaType TEXT_CSV = new MediaType("text", "csv", StandardCharsets.UTF_8);

    QueueEventsResponseConverter() {
        super(TEXT_CSV);
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return QueueEventsResponse.class.equals(aClass);
    }

    @Override
    protected QueueEventsResponse readInternal(Class<? extends QueueEventsResponse> aClass, HttpInputMessage httpInputMessage) throws HttpMessageNotReadableException {
        throw new HttpMessageNotReadableException("Reading history from CSV not supported", httpInputMessage);
    }

    @Override
    protected void writeInternal(QueueEventsResponse response, HttpOutputMessage output) throws IOException, HttpMessageNotWritableException {
        output.getHeaders().setContentType(TEXT_CSV);
        output.getHeaders().setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(String.format("%s.history.csv", response.getQueueName()))
                        .build());
        printEventsToCsv(response, output);
    }

    private void printEventsToCsv(QueueEventsResponse response, HttpOutputMessage output) throws IOException {
        CSVPrinter csvPrinter = new CSVPrinter(new OutputStreamWriter(output.getBody()), CSVFormat.DEFAULT);
        for (QueueEventsResponse.Event event : response.getEvents()) {
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
