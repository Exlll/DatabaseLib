rootProject.name = "databaselib"
include("DatabaseLib-Core")
findProject(":DatabaseLib-Core")?.name = "databaselib-core"
include("DatabaseLib-Bukkit")
findProject(":DatabaseLib-Bukkit")?.name = "databaselib-bukkit"
include("DatabaseLib-Bungee")
findProject(":DatabaseLib-Bungee")?.name = "databaselib-bungee"

