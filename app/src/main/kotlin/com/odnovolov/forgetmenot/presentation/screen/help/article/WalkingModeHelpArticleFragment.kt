package com.odnovolov.forgetmenot.presentation.screen.help.article

import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.presentation.common.di.AppDiScope
import com.odnovolov.forgetmenot.presentation.common.setTextWithClickableAnnotations
import com.odnovolov.forgetmenot.presentation.screen.help.HelpArticle
import kotlinx.android.synthetic.main.article.*

class WalkingModeHelpArticleFragment : BaseHelpArticleFragmentForSimpleUi() {
    override val layoutRes: Int get() = R.layout.article
    override val helpArticle: HelpArticle get() = HelpArticle.WalkingMode
    private val navigator by lazy { AppDiScope.get().navigator }

    override fun setupView() {
        articleContentTextView.setTextWithClickableAnnotations(
            stringId = R.string.article_walking_mode,
            onAnnotationClick = { annotationValue: String ->
                when (annotationValue) {
                    "walking_mode_settings" ->
                        navigator.navigateToWalkingModeSettingsFromWalkingModeArticle()
                }
            })
    }
}