package com.example.restservice.service;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidParameterException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import com.example.restservice.exceptions.SQInternalServerException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SecretsManager {

  private final JsonNode secrets;

  public SecretsManager(
      @Value("${aws.secretsmanager.secretName}") String secretName,
      @Value("${aws.secretsmanager.region}") String region) {
    if (System.getenv().get("SQ_ENV") == null) {
      // The secrets need to be loaded only on AWS. Skipping on local setup.
      secrets = null;
      return;
    }

    AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withRegion(region).build();

    GetSecretValueRequest getSecretValueRequest =
        new GetSecretValueRequest().withSecretId(secretName);
    GetSecretValueResult getSecretValueResult = null;

    try {
      getSecretValueResult = client.getSecretValue(getSecretValueRequest);
    } catch (DecryptionFailureException
        | InternalServiceErrorException
        | InvalidParameterException
        | InvalidRequestException
        | ResourceNotFoundException e) {
      throw new SQInternalServerException("Failed to get secrets from AWS: ", e);
    }

    if (getSecretValueResult.getSecretString() == null) {
      throw new SQInternalServerException("Unable to handle binary data");
    }

    try {
      secrets = new ObjectMapper().readTree(getSecretValueResult.getSecretString());
    } catch (JsonProcessingException e) {
      throw new SQInternalServerException("Bad Json String in Secret Value");
    }
  }

  public String getSecret(String key) {
    if (secrets == null) {
      // Secrets are read only if SQ_ENV env variable is set
      return "NOT_SET";
    }
    return secrets.get(key).asText();
  }
}
