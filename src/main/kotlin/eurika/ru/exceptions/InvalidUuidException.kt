package eurika.ru.exceptions

class InvalidUuidException(message: String = "В запросе не валидный UUID!") : Exception(message) {
}