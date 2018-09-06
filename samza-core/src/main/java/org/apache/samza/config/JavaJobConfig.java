package org.apache.samza.config;

public class JavaJobConfig extends JobConfig{

  public JavaJobConfig(Config config) {
    super(config);
  }

  public static String JOB_COORDINATOR_SYSTEM = JobConfig.JOB_COORDINATOR_SYSTEM();
}
