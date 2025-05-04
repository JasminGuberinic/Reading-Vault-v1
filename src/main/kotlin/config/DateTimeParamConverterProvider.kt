package org.virtualcode.config

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.ws.rs.ext.ParamConverter
import javax.ws.rs.ext.ParamConverterProvider
import javax.ws.rs.ext.Provider
import java.lang.reflect.Type

@Provider
class DateTimeParamConverterProvider : ParamConverterProvider {
    override fun <T> getConverter(
        rawType: Class<T>,
        genericType: Type?,
        annotations: Array<Annotation>?
    ): ParamConverter<T>? {
        return when (rawType) {
            LocalDateTime::class.java -> LocalDateTimeConverter() as ParamConverter<T>
            LocalDate::class.java -> LocalDateConverter() as ParamConverter<T>
            else -> null
        }
    }
}

class LocalDateTimeConverter : ParamConverter<LocalDateTime> {
    override fun fromString(value: String?): LocalDateTime? {
        return value?.let {
            LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
        }
    }

    override fun toString(value: LocalDateTime?): String? {
        return value?.format(DateTimeFormatter.ISO_DATE_TIME)
    }
}

class LocalDateConverter : ParamConverter<LocalDate> {
    override fun fromString(value: String?): LocalDate? {
        return value?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_DATE)
        }
    }

    override fun toString(value: LocalDate?): String? {
        return value?.format(DateTimeFormatter.ISO_DATE)
    }
}