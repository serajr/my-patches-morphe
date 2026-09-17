package app.template.patches.example

import app.morphe.patcher.patch.BytecodePatch
import app.morphe.patcher.patch.annotation.Patch
import app.template.patches.example.Fingerprints

@Patch(
    name = "Fix One UI Navigation Bar",
    description = "Força o reprodutor de vídeo do YouTube a respeitar a barra de navegação clássica com botões fixos da One UI.",
    dependencies = ["Morphe Core"] // Altere para a dependência padrão do app se necessário
)
object SamsungYTNavBarFixPatch : BytecodePatch(
    // Associas o patch à assinatura digital (fingerprint) do método do player que quero modificar
    Fingerprints.PLAYER_WINDOW_FLAG_METHOD 
) {
    override fun execute(context: BytecodeContext) {
        val method = context.method
        
        // Remove ou altera a instrução que força a janela a ficar "edge-to-edge" (tela cheia transparente)
        // Isso faz com que o app use o comportamento padrão do sistema operacional
        method.implementation.instructions.clear()
    }
}
