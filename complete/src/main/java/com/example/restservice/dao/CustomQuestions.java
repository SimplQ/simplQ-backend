package com.example.restservice.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CustomQuestions {

    @JsonProperty("questions")
    List<Question> questions;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Question {
        @JsonProperty("questionId")
        private String questionId;
        @JsonProperty("type")
        private String type;
        @JsonProperty("label")
        private String label;
        @JsonProperty("repeatable")
        private Boolean repeatable;
        @JsonProperty("subQuestions")
        private List<SubQuestion> subQuestions;

        public Question() {
            if(this.questionId == null) {
                this.questionId = UUID.randomUUID().toString();
            }
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Option {
            @JsonProperty("text")
            private String text;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class SubQuestion {
            @JsonProperty("questionId")
            private String questionId;
            @JsonProperty("label")
            private String label;
            @JsonProperty("type")
            private String type;
            @JsonProperty("options")
            private List<Option> options;

            public SubQuestion() {
                if(this.questionId == null) {
                    this.questionId = UUID.randomUUID().toString();
                }
            }
        }
    }
}
