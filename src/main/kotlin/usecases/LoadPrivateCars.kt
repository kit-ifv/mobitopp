package usecases

import modeling.steps.BasePrivateCarContext
import modeling.steps.Context
import modeling.steps.ModelExecution
import java.io.File

@Suppress("LongParameterList")
fun <S, C> S.preparePrivateCars(
    file: File? = null,

) where S : ModelExecution<C>, C : Context, C : BasePrivateCarContext {
    /**/
    val s = file?.name
    println(s)
}
