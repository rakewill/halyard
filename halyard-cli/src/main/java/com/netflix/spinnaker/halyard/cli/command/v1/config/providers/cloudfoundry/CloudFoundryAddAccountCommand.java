/*
 * Copyright 2019 Pivotal, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.cli.command.v1.config.providers.cloudfoundry;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.netflix.spinnaker.halyard.cli.command.v1.config.providers.account.AbstractAddAccountCommand;
import com.netflix.spinnaker.halyard.config.model.v1.node.Account;
import com.netflix.spinnaker.halyard.config.model.v1.providers.cloudfoundry.CloudFoundryAccount;

@Parameters(separators = "=")
public class CloudFoundryAddAccountCommand extends AbstractAddAccountCommand {
  @Parameter(
      names = {"--api-host", "--api"},
      required = true,
      description = CloudFoundryCommandProperties.API_HOST_DESCRIPTION)
  private String apiHost;

  @Parameter(
      names = {"--apps-manager-uri", "--appsManagerUri"},
      description = CloudFoundryCommandProperties.APPS_MANAGER_URI_DESCRIPTION)
  private String appsManagerUri;

  @Parameter(
      names = {"--metrics-uri", "--metricsUri"},
      description = CloudFoundryCommandProperties.METRICS_URI_DESCRIPTION)
  private String metricsUri;

  @Parameter(
      names = "--password",
      required = true,
      description = CloudFoundryCommandProperties.PASSWORD_DESCRIPTION)
  private String password;

  @Parameter(
      names = "--user",
      required = true,
      description = CloudFoundryCommandProperties.USER_DESCRIPTION)
  private String user;

  @Parameter(
      names = "--skip-ssl-validation",
      arity = 1,
      description = CloudFoundryCommandProperties.SKIP_SSL_VALIDATION_DESCRIPTION)
  private Boolean skipSslValidation = false;

  @Override
  protected Account buildAccount(String accountName) {
    CloudFoundryAccount cloudFoundryAccount =
        (CloudFoundryAccount) new CloudFoundryAccount().setName(accountName);
    return cloudFoundryAccount
        .setApiHost(apiHost)
        .setAppsManagerUri(appsManagerUri)
        .setMetricsUri(metricsUri)
        .setPassword(password)
        .setUser(user)
        .setSkipSslValidation(skipSslValidation);
  }

  @Override
  protected Account emptyAccount() {
    return new CloudFoundryAccount();
  }

  @Override
  protected String getProviderName() {
    return "cloudfoundry";
  }
}
