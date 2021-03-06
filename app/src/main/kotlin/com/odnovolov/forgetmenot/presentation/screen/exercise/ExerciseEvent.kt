package com.odnovolov.forgetmenot.presentation.screen.exercise

import com.odnovolov.forgetmenot.presentation.screen.walkingmodesettings.KeyGesture

sealed class ExerciseEvent {
    class PageSelected(val position: Int) : ExerciseEvent()
    object LevelOfKnowledgeButtonClicked : ExerciseEvent()
    class LevelOfKnowledgeSelected(val levelOfKnowledge: Int) : ExerciseEvent()
    object NotAskButtonClicked : ExerciseEvent()
    object AskAgainButtonClicked : ExerciseEvent()
    object SpeakButtonClicked : ExerciseEvent()
    object StopSpeakButtonClicked : ExerciseEvent()
    object EditCardButtonClicked : ExerciseEvent()
    object TimerButtonClicked : ExerciseEvent()
    object HintButtonClicked : ExerciseEvent()
    object HintAsQuizButtonClicked : ExerciseEvent()
    object MaskLettersButtonClicked : ExerciseEvent()
    object SearchButtonClicked : ExerciseEvent()
    object WalkingModeSettingsButtonClicked : ExerciseEvent()
    object WalkingModeHelpButtonClicked : ExerciseEvent()
    object WalkingModeSwitchToggled : ExerciseEvent()
    object HelpButtonClicked : ExerciseEvent()
    object FragmentResumed : ExerciseEvent()
    object FragmentPaused : ExerciseEvent()
    class KeyGestureDetected(val keyGesture: KeyGesture) : ExerciseEvent()
    object BackButtonClicked : ExerciseEvent()
    object ShowUnansweredCardButtonClicked : ExerciseEvent()
    object UserConfirmedExit : ExerciseEvent()
}