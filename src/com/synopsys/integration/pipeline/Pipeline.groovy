package com.synopsys.integration.pipeline

import com.synopsys.integration.pipeline.jenkins.ScriptWrapper
import com.synopsys.integration.pipeline.logging.DefaultPipelineLoger
import com.synopsys.integration.pipeline.logging.PipelineLogger
import com.synopsys.integration.pipeline.model.PipelineWrapper
import com.synopsys.integration.pipeline.model.Stage

class Pipeline implements Serializable {
    private final ScriptWrapper scriptWrapper
    private final PipelineLogger pipelineLogger

    private final List<PipelineWrapper> wrappers = new LinkedList<>()
    private final List<Stage> stages = new LinkedList<>()

    public Pipeline(Object script) {
        this.scriptWrapper = new ScriptWrapper(script)
        pipelineLogger = new DefaultPipelineLoger(scriptWrapper)
    }

    public void addStage(Stage stage) {
        pipelineLogger.info("Adding stage")
        stages.add(stage)
    }

    public void addPipelineWrapper(PipelineWrapper wrapper) {
        pipelineLogger.info("Adding wrapper")
        wrappers.add(wrapper)
    }

    public void run() {
        pipelineLogger.info("Starting run")
        wrappers.each { wrapper -> wrapper.start() }
        try {
            stages.each { currentStage ->
                scriptWrapper.stage(currentStage.getName(pipelineLogger)) {
                    pipelineLogger.info("running stage ${currentStage.getName(pipelineLogger)}")
                    currentStage.run()
                }
            }
        } catch (Exception e) {
            scriptWrapper.currentBuild().result = "FAILURE"
            wrappers.each { wrapper -> wrapper.handleException(e) }
            pipelineLogger.error("Build failed because ${e.getMessage()}")
            throw e
        } finally {
            wrappers.each { wrapper -> wrapper.end() }
        }
    }

}
