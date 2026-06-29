package com.hai265.timestamper.domain

class InvalidImportFileException(message: String, cause: Throwable? = null) :
    Exception(message, cause)