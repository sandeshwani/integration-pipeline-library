package com.synopsys.integration.pipeline.model

import com.synopsys.integration.pipeline.jenkins.PipelineConfiguration

abstract class PipelineWrapper extends Wrapper {
    PipelineWrapper(PipelineConfiguration pipelineConfiguration, String name) {
        super(pipelineConfiguration, name)
    }
}
