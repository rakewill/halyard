/*
 * Copyright 2018 Pivotal, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.config.validate.v1.providers.cloudfoundry;

import com.netflix.spinnaker.clouddriver.cloudfoundry.security.CloudFoundryCredentials;
import com.netflix.spinnaker.halyard.config.model.v1.node.Validator;
import com.netflix.spinnaker.halyard.config.model.v1.providers.cloudfoundry.CloudFoundryAccount;
import com.netflix.spinnaker.halyard.config.problem.v1.ConfigProblemSetBuilder;
import com.netflix.spinnaker.halyard.core.problem.v1.Problem;
import com.netflix.spinnaker.halyard.core.tasks.v1.DaemonTaskHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Data
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class CloudFoundryAccountValidator extends Validator<CloudFoundryAccount> {
  @Override
  public void validate(
      ConfigProblemSetBuilder problemSetBuilder, CloudFoundryAccount cloudFoundryAccount) {
    String accountName = cloudFoundryAccount.getName();

    DaemonTaskHandler.message(
        "Validating "
            + accountName
            + " with "
            + CloudFoundryAccountValidator.class.getSimpleName());

    String environment = cloudFoundryAccount.getEnvironment();
    String apiHost = cloudFoundryAccount.getApiHost();
    String appsManagerUri = cloudFoundryAccount.getAppsManagerUri();
    String metricsUri = cloudFoundryAccount.getMetricsUri();
    String password = cloudFoundryAccount.getPassword();
    String user = cloudFoundryAccount.getUser();
    boolean skipSslValidation = cloudFoundryAccount.isSkipSslValidation();

    if (StringUtils.isEmpty(user) || StringUtils.isEmpty(password)) {
      problemSetBuilder.addProblem(
          Problem.Severity.ERROR, "You must provide a user and a password");
    }

    if (StringUtils.isEmpty(apiHost)) {
      problemSetBuilder.addProblem(
          Problem.Severity.ERROR, "You must provide a CF api endpoint host");
    }

    if (StringUtils.isEmpty(appsManagerUri)) {
      problemSetBuilder.addProblem(
          Problem.Severity.WARNING,
          "To be able to link server groups to CF Appsmanager a URI is required: " + accountName);
    }

    if (StringUtils.isEmpty(metricsUri)) {
      problemSetBuilder.addProblem(
          Problem.Severity.WARNING,
          "To be able to link server groups to CF Metrics a URI is required: " + accountName);
    }

    if (skipSslValidation) {
      problemSetBuilder.addProblem(
          Problem.Severity.WARNING,
          "SKIPPING SSL server certificate validation of the CloudFoundry API endpoint for account: "
              + accountName);
    }

    CloudFoundryCredentials cloudFoundryCredentials =
        new CloudFoundryCredentials(
            cloudFoundryAccount.getName(),
            appsManagerUri,
            metricsUri,
            apiHost,
            user,
            password,
            environment,
            skipSslValidation);

    try {
      int count = cloudFoundryCredentials.getCredentials().getSpaces().all().size();
      log.debug("Retrieved {} spaces using account {}", count, accountName);
    } catch (Exception e) {
      problemSetBuilder.addProblem(
          Problem.Severity.ERROR,
          "Failed to fetch spaces while validating account '"
              + accountName
              + "': "
              + e.getMessage()
              + ".");
    }
  }
}
