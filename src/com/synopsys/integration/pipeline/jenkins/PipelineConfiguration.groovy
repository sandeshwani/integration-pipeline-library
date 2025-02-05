package com.synopsys.integration.pipeline.jenkins

import com.synopsys.integration.pipeline.logging.PipelineLogger

class PipelineConfiguration implements Serializable {
    public PipelineLogger logger
    public JenkinsScriptWrapper scriptWrapper

    public PipelineConfiguration(PipelineLogger logger, JenkinsScriptWrapper scriptWrapper) {
        this.logger = logger
        this.scriptWrapper = scriptWrapper
    }

    public JenkinsScriptWrapper getScriptWrapper() {
        return scriptWrapper
    }

    public void setScriptWrapper(final JenkinsScriptWrapper scriptWrapper) {
        this.scriptWrapper = scriptWrapper
    }

    public PipelineLogger getLogger() {
        return logger
    }

    void setLogger(final PipelineLogger logger) {
        this.logger = logger
    }
}
