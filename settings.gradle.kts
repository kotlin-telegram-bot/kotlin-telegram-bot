rootProject.name = "kotlin-telegram-bot"

include(":telegram", ":echo", ":dispatcher", ":polls", ":webhook")

project(":echo").projectDir = File(rootDir, "samples/echo")
project(":dispatcher").projectDir = File(rootDir, "samples/dispatcher")
project(":polls").projectDir = File(rootDir, "samples/polls")
project(":webhook").projectDir = File(rootDir, "samples/webhook")
