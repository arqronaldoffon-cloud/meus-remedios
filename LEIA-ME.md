# Meus Remédios — aplicativo Android

App para idosos: lembrete de remédios com alarme (toca com o celular bloqueado),
controle de estoque, registro de pressão, voz em português e botão SOS (192, 193, 190).

## Gerar o APK de graça (sem instalar nada no computador)

1. Crie uma conta gratuita em **github.com**.
2. Clique em **New repository** → nome `meus-remedios` → marque **Public** → **Create repository**.
3. Clique em **uploading an existing file**.
4. Abra a pasta `MeusRemedios` (descompactada), selecione **tudo o que está dentro dela**
   (inclusive a pasta `.github`) e arraste para a página. Clique em **Commit changes**.
5. Abra a aba **Actions**: o GitHub começa a montar o app sozinho (uns 5 minutos).
6. Quando ficar verde ✅, abra **Releases** (lado direito da página do repositório).
   Lá está o arquivo **MeusRemedios.apk**. Esse link pode ser enviado pelo WhatsApp.

Sempre que um arquivo for atualizado no GitHub, um APK novo é gerado automaticamente.

## Instalar no celular

1. Abra o link e baixe o **MeusRemedios.apk**.
2. Toque no arquivo e em **Instalar**. Se aparecer aviso, permita "instalar apps desta fonte".
   O Google pode avisar que o app é desconhecido — é normal para apps fora da Play Store.
3. Abra o app e **permita as notificações**.
4. Em **⚙️ Ajustes**, toque em **🔋 Liberar bateria para o app** e escolha "Não otimizar"
   / "Sem restrições". Sem isso alguns celulares (Samsung, Xiaomi, Motorola) atrasam o alarme.
5. Cadastre um remédio para daqui a 2 minutos, bloqueie o celular e confira se o alarme toca.

## Alternativa: Android Studio (gratuito)

Abra a pasta no Android Studio → espere sincronizar → menu **Build → Build APK(s)**.

## Observações

- O app não substitui orientação médica. As faixas de pressão são referência geral.
- O botão SOS abre o discador já com o número; a pessoa só confirma a ligação.
- Os dados ficam só no celular de cada pessoa.
