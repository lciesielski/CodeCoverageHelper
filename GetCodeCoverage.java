import com.sforce.async.*;
import com.sforce.bulk.*;
import com.sforce.ws.*;
import com.sforce.soap.partner.*;
import com.sforce.soap.metadata.*;

import com.google.common.collect.*;

import java.text.DecimalFormat;
import java.util.List;

public class GetCodeCoverage {

	private final static Integer ARGUMENT_USERNAME = 0;
	private final static Integer ARGUMENT_PASSWORD = 1;
	private final static Integer ARGUMENT_ENDPOINT = 2;
	private final static Integer ARGUMENT_MINIMUM_COVERAGE = 3;
	private final static Integer ARGUMENT_DEPLOYMENT_ID = 4;
	private final static Integer ARGUMENT_DEBUG = 5;

	public static void main(String[] args) {
		if (Boolean.parseBoolean(args[ARGUMENT_DEBUG])) {
			System.out.println("ARGUMENT_USERNAME : " + args[ARGUMENT_USERNAME]);
			System.out.println("ARGUMENT_PASSWORD : " + args[ARGUMENT_PASSWORD]);
			System.out.println("ARGUMENT_ENDPOINT : " + args[ARGUMENT_ENDPOINT]);
			System.out.println("ARGUMENT_MINIMUM_COVERAGE : " + args[ARGUMENT_MINIMUM_COVERAGE]);
			System.out.println("ARGUMENT_DEPLOYMENT_ID : " + args[ARGUMENT_DEPLOYMENT_ID]);
			System.out.println("ARGUMENT_DEBUG : " + args[ARGUMENT_DEBUG]);
		}

		checkCodeCoverage(
			login(args),
			args
		);
	}

	public static LoginResult login(String[] args) {
		LoginResult loginResult = null;

		try {
			final ConnectorConfig config = new ConnectorConfig();
			config.setAuthEndpoint(args[ARGUMENT_ENDPOINT]);
			config.setServiceEndpoint(args[ARGUMENT_ENDPOINT]);
			config.setManualLogin(true);
			
			final PartnerConnection connection = new PartnerConnection(config);
			loginResult = connection.login(
				args[ARGUMENT_USERNAME], 
				args[ARGUMENT_PASSWORD]
			);

			if (Boolean.parseBoolean(args[ARGUMENT_DEBUG])) {
				System.out.println("User ID: " + loginResult.getUserId());
				System.out.println("User Full Name: " + loginResult.getUserInfo().getUserFullName());
				System.out.println();
				System.out.println("Session ID: " + loginResult.getSessionId());
				System.out.println("Server URL: " + loginResult.getServerUrl());
				System.out.println("Metadata Server URL: " + loginResult.getMetadataServerUrl());
				System.out.println();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}

		return loginResult;
	}

	public static void checkCodeCoverage(LoginResult loginResponse, String[] args) {
		try {
			if (loginResponse != null) {
				final double minimumCoverage = Double.parseDouble(args[ARGUMENT_MINIMUM_COVERAGE]);
				final boolean includeDetails = true;
				final ConnectorConfig config = new ConnectorConfig();
				config.setServiceEndpoint(loginResponse.getMetadataServerUrl());
				config.setSessionId(loginResponse.getSessionId());

				final DeployResult deployResult = new MetadataConnection(config).checkDeployStatus(
					args[ARGUMENT_DEPLOYMENT_ID], 
					includeDetails
				);

				final DeployDetails details = deployResult.getDetails();
				if (details != null) {
					final double coverage = getCoverageTotal(details.getRunTestResult().getCodeCoverage());

					final DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(2);
					df.setMinimumFractionDigits(2);

					final String coverageString = df.format(coverage);

					if (coverage < minimumCoverage) {
						System.out.println(
							"Coverage Failure: Code coverage is below minimal requirement of " + 
							minimumCoverage + 
							" and is equal to " + 
							coverage
						);

						System.exit(1);
					} else {
						System.out.println(
							"Coverage Success! Code coverage is above minimal requirement of " +  
							minimumCoverage +
							" and is equal to " + 
							coverageString
						);

						System.exit(0);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private static double getCoverageTotal(CodeCoverageResult[] codeCoverageResults) {
		final List<Integer> resultingTotalLines = Lists.newArrayList();
		final List<Integer> resultingCoveredLines = Lists.newArrayList();

		for (CodeCoverageResult currentResult : codeCoverageResults) {
			final Integer totalLines = currentResult.getNumLocations();
			final Integer covered = totalLines - currentResult.getNumLocationsNotCovered();
			
			resultingTotalLines.add(totalLines);
			resultingCoveredLines.add(covered);
		}

		final Integer totalLineCount = resultingTotalLines.stream().mapToInt(Integer::intValue).sum();
		final Integer totalCoveredCount = resultingCoveredLines.stream().mapToInt(Integer::intValue).sum();
		
		return totalCoveredCount * 100.0f / totalLineCount;
	}

}