# QuiZoeira App - Aplicativo Android

Este é o projeto completo do aplicativo Android **QuiZoeira**, que carrega o site https://x9quiz.stunnelpro.shop/ em um WebView com funcionalidades avançadas.

## Funcionalidades Incluídas

✅ **WebView** que carrega o site QuiZoeira
✅ **Serviço em Segundo Plano** para manter a rádio tocando
✅ **Splash Screen** personalizada com cores harmoniosas
✅ **Ícone de App Profissional** com design adaptativo
✅ **Notificação Customizada** com controles de Play, Pause e Fechar
✅ **Funcionalidade "Puxe para Atualizar"** (Pull-to-Refresh)
✅ **Barra de Progresso** de carregamento no topo

## Requisitos para Compilação

Para compilar este projeto em uma VM Debian 12, você precisará de:

- Java Development Kit (JDK) 17
- Android SDK (Command Line Tools)
- Gradle (incluído no projeto via Gradle Wrapper)

## Guia de Compilação no Debian 12

### 1. Instalar Java e Ferramentas Essenciais

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y wget unzip openjdk-17-jdk
```

### 2. Configurar o Android SDK

```bash
# Criar diretórios para o SDK
mkdir -p $HOME/android/sdk

# Baixar as ferramentas de linha de comando
cd $HOME/android
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip

# Descompactar e organizar
unzip commandlinetools-linux-*.zip
rm commandlinetools-linux-*.zip
mkdir -p $HOME/android/sdk/cmdline-tools
mv cmdline-tools $HOME/android/sdk/cmdline-tools/latest
```

### 3. Configurar Variáveis de Ambiente

```bash
# Adicionar ao ~/.bashrc
echo '' >> ~/.bashrc
echo '# Android environment variables' >> ~/.bashrc
echo 'export ANDROID_HOME=$HOME/android/sdk' >> ~/.bashrc
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> ~/.bashrc

# Aplicar as mudanças
source ~/.bashrc
```

### 4. Instalar Plataformas e Ferramentas de Compilação

```bash
# Aceitar licenças
yes | sdkmanager --licenses

# Baixar componentes necessários
sdkmanager "platforms;android-33" "build-tools;33.0.2"
```

### 5. Compilar o APK

```bash
# Navegar até a pasta do projeto
cd QuiZoeiraApp

# Dar permissão de execução ao gradlew (se necessário)
chmod +x ./gradlew

# Compilar o APK de depuração
./gradlew assembleDebug
```

### 6. Encontrar o APK

Após a compilação bem-sucedida, o APK estará em:

```
QuiZoeiraApp/app/build/outputs/apk/debug/app-debug.apk
```

Este arquivo pode ser transferido para um celular Android e instalado diretamente.

## Instalação no Celular

1. Transfira o arquivo `app-debug.apk` para o seu celular Android
2. Nas configurações do Android, habilite "Instalar apps de fontes desconhecidas"
3. Abra o arquivo APK e siga as instruções de instalação
4. Na primeira execução, o app solicitará permissão para "Sobrepor a outros apps" - conceda esta permissão para que a rádio continue tocando em segundo plano

## Estrutura do Projeto

```
QuiZoeiraApp/
├── app/
│   ├── build.gradle                    # Configuração de build do módulo app
│   └── src/
│       └── main/
│           ├── java/                   # Código-fonte Java
│           ├── res/                    # Recursos (layouts, ícones, strings)
│           └── AndroidManifest.xml     # Manifesto do app
├── build.gradle                        # Configuração de build raiz
├── settings.gradle                     # Configurações do projeto
├── gradle.properties                   # Propriedades do Gradle
├── gradlew                            # Script Gradle Wrapper (Linux/Mac)
└── gradle/
    └── wrapper/                       # Arquivos do Gradle Wrapper
```

## Informações Técnicas

- **Package Name:** com.meuapp.webview
- **Min SDK:** 21 (Android 5.0 Lollipop)
- **Target SDK:** 33 (Android 13)
- **Version Code:** 1
- **Version Name:** 1.0

## Suporte

Para dúvidas ou problemas com a compilação, verifique se todos os passos foram seguidos corretamente e se as variáveis de ambiente foram configuradas adequadamente.

---

**Desenvolvido para o site QuiZoeira** 🎵🎯
