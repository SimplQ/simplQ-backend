package me.simplq.controller.model.queue;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class QueueEventsCsvResponse {
    String fileName;
    QueueEventsResponse eventResponse;
}
