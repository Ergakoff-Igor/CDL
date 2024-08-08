package ru.docs.construction.manager.entity;

/**
 * Модель акта
 */

public record Act(Long id, String month, Short year, String section, Double price, ActStatus actStatus) {
    /**
     * Метод конвертации статуса акта в string для шаблонизатора
     *
     * @return строковое значение статуса
     */
    public String actStatusToString() {
        return this.actStatus.toString();
    }
}
