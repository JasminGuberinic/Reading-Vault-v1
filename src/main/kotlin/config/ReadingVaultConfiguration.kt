package org.virtualcode.config

import io.dropwizard.Configuration
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotEmpty

class ReadingVaultConfiguration : Configuration() {

    @JsonProperty("databaseUrl")
    @NotEmpty
    var databaseUrl: String = ""

    @JsonProperty("databaseUser")
    var databaseUser: String = ""

    @JsonProperty("databasePassword")
    var databasePassword: String = ""

    @JsonProperty("maxPoolSize")
    var maxPoolSize: Int = 10

    @JsonProperty("minIdle")
    var minIdle: Int = 2

    @JsonProperty("idleTimeout")
    var idleTimeout: Long = 60000

    @JsonProperty("connectionTimeout")
    var connectionTimeout: Long = 30000

    @JsonProperty("autoCommit")
    var autoCommit: Boolean = false

    @JsonProperty("transactionIsolation")
    var transactionIsolation: String = "TRANSACTION_REPEATABLE_READ"

    @JsonProperty("swaggerBasePath")
    var swaggerBasePath: String? = null

    @JsonProperty("enableH2Console")
    var enableH2Console: Boolean = true
}