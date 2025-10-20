package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.TuningResult
import com.example.myapplication.domain.repository.TunerRepository

/**
 * UseCase для анализа ноты.
 * Отвечает за получение данных о текущей частоте и отклонении струны через репозиторий.
 *
 * UseCase — слой бизнес-логики, который отделяет работу с репозиторием
 * от UI или других компонентов приложения.
 */
class DetectNoteUseCase(
    private val repository: TunerRepository // Репозиторий, который предоставляет функционал тюнера
) {
    /**
     * Вызывает репозиторий для анализа текущей ноты.
     *
     * suspend — метод асинхронный, т.к. анализ может включать запись аудио и вычисления,
     * которые нельзя выполнять в основном потоке.
     *
     * @return TuningResult — результат анализа ноты:
     *         - ближайшая эталонная нота
     *         - отклонение в центах
     *         - флаг, настроена ли струна
     *         - определённая частота
     */
    suspend operator fun invoke(): TuningResult {
        // Просто делегируем работу репозиторию
        return repository.analyze()
    }
}
