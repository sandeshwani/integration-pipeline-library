package com.synopsys.integration.pipeline.generic

import com.cloudbees.groovy.cps.NonCPS
import com.synopsys.integration.pipeline.exception.PipelineException
import com.synopsys.integration.pipeline.model.Stage
import org.jenkinsci.plugins.workflow.cps.CpsScript

class ClosureStage extends Stage {
    private final CpsScript closure

    ClosureStage(final String name, CpsScript closure) {
        super(name)
        this.closure = closure

    }

    @NonCPS
    @Override
    void stageExecution() throws PipelineException, Exception {
        closure.run()
    }
}
