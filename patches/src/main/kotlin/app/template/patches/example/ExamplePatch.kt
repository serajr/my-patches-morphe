package app.template.patches.example

import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotation.Patch
import app.revanced.patcher.context.BytecodeContext
import app.template.patches.example.YouTubePlayerViewFingerprint

@Patch(
    name = "Fix One UI Navigation Bar",
    description = "Força o reprodutor de vídeo do YouTube a adicionar padding inferior correspondente à altura real dos botões fixos da One UI.",
    dependencies = []
)
object ExamplePatch : BytecodePatch(setOf(YouTubePlayerViewFingerprint)) {
    
    override fun execute(context: BytecodeContext) {
        // Encontra o método mapeado no passo 1 através do fingerprint local
        YouTubePlayerViewFingerprint.methodOrNull?.apply {
            
            // Captura o índice da primeira instrução correspondente
            val targetIndex = YouTubePlayerViewFingerprint.instructionMatches.first().index
            
            // Injeta o bytecode Smali cirurgicamente na classe do player do YouTube
            addInstructions(
                targetIndex,
                """
                # Captura a view atual e força o cálculo das margens físicas dos botões da One UI
                invoke-static {p0}, Landroidx/core/view/ViewCompat;->getRootWindowInsets(Landroid/view/View;)Landroidx/core/view/WindowInsetsCompat;
                move-result-object v0
                if-eqz v0, :cond_skip
                
                # Pega o tipo de insets das system bars (barra de botões)
                invoke-static {}, Landroidx/core/view/WindowInsetsCompat${"$"}Type;->systemBars()I
                move-result v1
                invoke-virtual {v0, v1}, Landroidx/core/view/WindowInsetsCompat;->getInsets(I)Landroidx/core/graphics/Insets;
                move-result-object v0
                
                # Extrai o campo .bottom (altura em pixels dos botões fixos)
                iget v0, v0, Landroidx/core/graphics/Insets;->bottom:I
                if-gtz v0, :cond_skip
                
                # Se houver botões fixos ativos, aplica padding inferior na View do Player do YouTube
                invoke-virtual {p0}, Landroid/view/View;->getPaddingLeft()I
                move-result v1
                invoke-virtual {p0}, Landroid/view/View;->getPaddingTop()I
                move-result v2
                invoke-virtual {p0}, Landroid/view/View;->getPaddingRight()I
                move-result v3
                invoke-virtual {p0, v1, v2, v3, v0}, Landroid/view/View;->setPadding(IIII)V
                
                :cond_skip
                """
            )
        }
    }
}
