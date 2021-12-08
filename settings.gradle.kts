rootProject.name = "workflow"

include(
    ":internal-testing-utils",
    ":samples:compose-samples",
    ":samples:containers:app-poetry",
    ":samples:containers:app-raven",
    ":samples:containers:android",
    ":samples:containers:common",
    ":samples:containers:hello-back-button",
    ":samples:containers:poetry",
    ":samples:dungeon:app",
    ":samples:dungeon:common",
    ":samples:dungeon:timemachine",
    ":samples:dungeon:timemachine-shakeable",
    ":samples:hello-terminal:hello-terminal-app",
    ":samples:hello-terminal:terminal-workflow",
    ":samples:hello-terminal:todo-terminal-app",
    ":samples:hello-workflow",
    ":samples:hello-workflow-fragment",
    ":samples:stub-visibility",
    ":samples:tictactoe:app",
    ":samples:tictactoe:common",
    ":samples:todo-android:app",
    ":trace-encoder",
    ":workflow-core",
    ":workflow-core-compose",
    ":workflow-runtime",
    ":workflow-rx2",
    ":workflow-testing",
    ":workflow-tracing",
    ":workflow-ui:compose",
    ":workflow-ui:compose-tooling",
    ":workflow-ui:core-common",
    ":workflow-ui:core-android",
    ":workflow-ui:internal-testing-android",
    ":workflow-ui:container-common",
    ":workflow-ui:container-android",
    ":workflow-ui:radiography"
)

// Include the tutorial build so the IDE sees it when syncing the main project.
includeBuild("samples/tutorial")
