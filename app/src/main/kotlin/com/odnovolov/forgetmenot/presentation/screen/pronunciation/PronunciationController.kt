package com.odnovolov.forgetmenot.presentation.screen.pronunciation

import com.odnovolov.forgetmenot.domain.architecturecomponents.EventFlow
import com.odnovolov.forgetmenot.domain.checkPronunciationName
import com.odnovolov.forgetmenot.domain.entity.GlobalState
import com.odnovolov.forgetmenot.domain.entity.NameCheckResult
import com.odnovolov.forgetmenot.domain.entity.Pronunciation
import com.odnovolov.forgetmenot.domain.interactor.decksettings.DeckSettings
import com.odnovolov.forgetmenot.domain.interactor.decksettings.PronunciationSettings
import com.odnovolov.forgetmenot.presentation.common.LongTermStateSaver
import com.odnovolov.forgetmenot.presentation.common.UserSessionTermStateProvider
import com.odnovolov.forgetmenot.presentation.common.entity.NamePresetDialogStatus.*
import com.odnovolov.forgetmenot.presentation.screen.pronunciation.PronunciationController.Command.SetNamePresetDialogText
import kotlinx.coroutines.flow.Flow
import java.util.*

class PronunciationController(
    private val deckSettingsState: DeckSettings.State,
    private val pronunciationSettings: PronunciationSettings,
    private val pronunciationScreenState: PronunciationScreenState,
    private val globalState: GlobalState,
    private val longTermStateSaver: LongTermStateSaver,
    private val pronunciationScreenStateProvider: UserSessionTermStateProvider<PronunciationScreenState>
) {
    sealed class Command {
        class SetNamePresetDialogText(val text: String) : Command()
    }

    private val commandFlow = EventFlow<Command>()
    val commands: Flow<Command> = commandFlow.get()

    fun onSavePronunciationButtonClicked() {
        pronunciationScreenState.namePresetDialogStatus = VisibleToMakeIndividualPresetAsShared
        commandFlow.send(SetNamePresetDialogText(""))
    }

    fun onSetPronunciationButtonClicked(pronunciationId: Long) {
        pronunciationSettings.setPronunciation(pronunciationId)
        longTermStateSaver.saveStateByRegistry()
    }

    fun onRenamePronunciationButtonClicked(pronunciationId: Long) {
        pronunciationScreenState.renamePresetId = pronunciationId
        pronunciationScreenState.namePresetDialogStatus = VisibleToRenameSharedPreset
        globalState.sharedPronunciations.find { it.id == pronunciationId }
            ?.name
            ?.let { pronunciationName: String ->
                commandFlow.send(SetNamePresetDialogText(pronunciationName))
            }
    }

    fun onDeletePronunciationButtonClicked(pronunciationId: Long) {
        pronunciationSettings.deleteSharedPronunciation(pronunciationId)
        longTermStateSaver.saveStateByRegistry()
    }

    fun onAddNewPronunciationButtonClicked() {
        pronunciationScreenState.namePresetDialogStatus = VisibleToCreateNewSharedPreset
        commandFlow.send(SetNamePresetDialogText(""))
    }

    fun onDialogTextChanged(text: String) {
        pronunciationScreenState.typedPresetName = text
    }

    fun onNamePresetPositiveDialogButtonClicked() {
        val newPresetName: String = pronunciationScreenState.typedPresetName
        if (checkPronunciationName(newPresetName, globalState) != NameCheckResult.Ok) return
        when (pronunciationScreenState.namePresetDialogStatus) {
            VisibleToMakeIndividualPresetAsShared -> {
                val pronunciation = deckSettingsState.deck.exercisePreference.pronunciation
                pronunciationSettings.renamePronunciation(pronunciation, newPresetName)
            }
            VisibleToCreateNewSharedPreset -> {
                pronunciationSettings.createNewSharedPronunciation(newPresetName)
            }
            VisibleToRenameSharedPreset -> {
                globalState.sharedPronunciations
                    .find { it.id == pronunciationScreenState.renamePresetId }
                    ?.let { pronunciation: Pronunciation ->
                        pronunciationSettings.renamePronunciation(pronunciation, newPresetName)
                    }
            }
            Invisible -> {
            }
        }
        pronunciationScreenState.namePresetDialogStatus = Invisible
        longTermStateSaver.saveStateByRegistry()
    }

    fun onNamePresetNegativeDialogButtonClicked() {
        pronunciationScreenState.namePresetDialogStatus = Invisible
    }

    fun onQuestionLanguageSelected(language: Locale?) {
        pronunciationSettings.setQuestionLanguage(language)
        longTermStateSaver.saveStateByRegistry()
    }

    fun onQuestionAutoSpeakSwitchToggled() {
        val newQuestionAutoSpeak: Boolean =
            deckSettingsState.deck.exercisePreference.pronunciation.questionAutoSpeak.not()
        pronunciationSettings.setQuestionAutoSpeak(newQuestionAutoSpeak)
        longTermStateSaver.saveStateByRegistry()
    }

    fun onAnswerLanguageSelected(language: Locale?) {
        pronunciationSettings.setAnswerLanguage(language)
        longTermStateSaver.saveStateByRegistry()
    }

    fun onAnswerAutoSpeakSwitchToggled() {
        val newAnswerAutoSpeak: Boolean =
            deckSettingsState.deck.exercisePreference.pronunciation.answerAutoSpeak.not()
        pronunciationSettings.setAnswerAutoSpeak(newAnswerAutoSpeak)
        longTermStateSaver.saveStateByRegistry()
    }

    fun onDoNotSpeakTextInBracketsSwitchToggled() {
        val newDoNotSpeakTextInBrackets: Boolean =
            deckSettingsState.deck.exercisePreference.pronunciation.doNotSpeakTextInBrackets.not()
        pronunciationSettings.setDoNotSpeakTextInBrackets(newDoNotSpeakTextInBrackets)
        longTermStateSaver.saveStateByRegistry()
    }

    fun onFragmentPause() {
        pronunciationScreenStateProvider.save(pronunciationScreenState)
    }
}