package com.odnovolov.forgetmenot.domain.feature.exercise

import com.badoo.mvicore.element.*
import com.badoo.mvicore.feature.BaseFeature
import com.odnovolov.forgetmenot.domain.feature.exercise.ExerciseFeature.*
import com.odnovolov.forgetmenot.domain.feature.exercise.ExerciseFeature.Action.FulFill
import com.odnovolov.forgetmenot.domain.feature.exercise.ExerciseFeature.Action.ProcessNewExerciseData
import com.odnovolov.forgetmenot.domain.feature.exercise.ExerciseFeature.Effect.*
import com.odnovolov.forgetmenot.domain.feature.exercise.ExerciseFeature.Wish.SetActiveCardPosition
import com.odnovolov.forgetmenot.domain.feature.exercise.ExerciseFeature.Wish.ShowAnswer
import com.odnovolov.forgetmenot.domain.repository.ExerciseRepository
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class ExerciseFeature(
    exerciseRepository: ExerciseRepository,
    mainThreadScheduler: Scheduler
) : BaseFeature<Wish, Action, Effect, State, News>(
    initialState = State(),
    wishToAction = { wish -> FulFill(wish) },
    bootstrapper = BootstrapperImpl(exerciseRepository, mainThreadScheduler),
    actor = ActorImpl(exerciseRepository, mainThreadScheduler),
    reducer = ReducerImpl(),
    postProcessor = PostProcessorImpl(),
    newsPublisher = NewsPublisherImpl()
) {
    class BootstrapperImpl(
        private val exerciseRepository: ExerciseRepository,
        private val mainThreadScheduler: Scheduler
    ) : Bootstrapper<Action> {
        override fun invoke(): Observable<Action> {
            return exerciseRepository.observeExercise()
                .map { exerciseData: ExerciseData -> ProcessNewExerciseData(exerciseData) as Action }
                .subscribeOn(Schedulers.io())
                .observeOn(mainThreadScheduler)
        }
    }

    sealed class Action {
        data class FulFill(val wish: Wish) : Action()
        data class ProcessNewExerciseData(val exerciseData: ExerciseData) : Action()
    }

    sealed class Wish {
        data class SetActiveCardPosition(val position: Int) : Wish()
        object ShowAnswer : Wish()
    }

    class ActorImpl(
        private val exerciseRepository: ExerciseRepository,
        private val mainThreadScheduler: Scheduler
    ) : Actor<State, Action, Effect> {
        override fun invoke(state: State, action: Action): Observable<Effect> {
            return when (action) {
                is FulFill -> when (action.wish) {
                    is SetActiveCardPosition -> {
                        val effect: Effect = ChangeActiveCardPosition(action.wish.position)
                        Observable.just(effect)
                    }
                    is ShowAnswer -> Observable.fromCallable {
                        val activeExerciseCard = state.exerciseData.exerciseCards[state.activeCardPosition!!]
                        val exerciseCardToUpdate = activeExerciseCard.copy(isAnswered = true)
                        exerciseRepository.updateExerciseCard(exerciseCardToUpdate)
                    }
                        .map { AnswerShowed as Effect }
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThreadScheduler)
                }
                is ProcessNewExerciseData -> Observable.just(NewExerciseDataGot(action.exerciseData) as Effect)
            }
        }
    }

    sealed class Effect {
        data class NewExerciseDataGot(val exerciseData: ExerciseData) : Effect()
        object AnswerShowed : Effect()
        data class ChangeActiveCardPosition(val position: Int) : Effect()
    }

    class ReducerImpl : Reducer<State, Effect> {
        override fun invoke(state: State, effect: Effect): State {
            return when (effect) {
                is NewExerciseDataGot -> state.copy(exerciseData = effect.exerciseData)
                is AnswerShowed -> state
                is ChangeActiveCardPosition -> state.copy(activeCardPosition = effect.position)
            }
        }
    }

    data class State(
        val exerciseData: ExerciseData = ExerciseData(),
        val activeCardPosition: Int? = null
    )

    class PostProcessorImpl : PostProcessor<Action, Effect, State> {
        override fun invoke(action: Action, effect: Effect, state: State): Action? {
            return null
        }
    }

    class NewsPublisherImpl : NewsPublisher<Action, Effect, State, News> {
        override fun invoke(action: Action, effect: Effect, state: State): News? {
            return null
        }
    }

    sealed class News
}