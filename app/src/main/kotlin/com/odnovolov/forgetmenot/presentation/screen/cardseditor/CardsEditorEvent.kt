package com.odnovolov.forgetmenot.presentation.screen.cardseditor

sealed class CardsEditorEvent {
    class PageSelected(val position: Int) : CardsEditorEvent()
    object LevelOfKnowledgeButtonClicked : CardsEditorEvent()
    class LevelOfKnowledgeSelected(val levelOfKnowledge: Int) : CardsEditorEvent()
    object NotAskButtonClicked : CardsEditorEvent()
    object AskAgainButtonClicked : CardsEditorEvent()
    object RemoveCardButtonClicked : CardsEditorEvent()
    object RestoreLastRemovedCardButtonClicked : CardsEditorEvent()
    object HelpButtonClicked : CardsEditorEvent()
    object CancelButtonClicked : CardsEditorEvent()
    object DoneButtonClicked : CardsEditorEvent()
    object BackButtonClicked : CardsEditorEvent()
    object UserConfirmedExit : CardsEditorEvent()
}