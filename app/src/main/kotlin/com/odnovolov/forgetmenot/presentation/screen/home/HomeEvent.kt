package com.odnovolov.forgetmenot.presentation.screen.home

import java.io.OutputStream

sealed class HomeEvent {
    class SearchTextChanged(val searchText: String) : HomeEvent()
    object SearchInCardsButtonClicked : HomeEvent()
    object DisplayOnlyWithTasksCheckboxClicked : HomeEvent()
    object SettingsButtonClicked : HomeEvent()
    object HelpButtonClicked : HomeEvent()
    class DeckButtonClicked(val deckId: Long) : HomeEvent()
    class DeckButtonLongClicked(val deckId: Long) : HomeEvent()
    class RepetitionModeMenuItemClicked(val deckId: Long) : HomeEvent()
    object RepetitionModeMultiSelectMenuItemClicked : HomeEvent()
    class SetupDeckMenuItemClicked(val deckId: Long) : HomeEvent()
    class ExportMenuItemClicked(val deckId: Long) : HomeEvent()
    class OutputStreamOpened(val outputStream: OutputStream) : HomeEvent()
    class RemoveDeckMenuItemClicked(val deckId: Long) : HomeEvent()
    object DecksRemovedSnackbarCancelActionClicked : HomeEvent()
    object StartExerciseMenuItemClicked : HomeEvent()
    object SelectAllDecksMenuItemClicked : HomeEvent()
    object RemoveDecksMenuItemClicked : HomeEvent()
    object ActionModeFinished : HomeEvent()
}