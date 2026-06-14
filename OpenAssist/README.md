# OpenAssist

OpenAssist is a native Android AI assistant prototype built with Kotlin, Jetpack Compose, MVVM, Retrofit, OkHttp, Coroutines, StateFlow, EncryptedSharedPreferences, and Material 3.

## Principles

- Bring your own OpenRouter API key.
- No backend, Firebase, Supabase, user accounts, cloud database, or subscription system.
- Store API keys, selected model, and preferences locally on the device.
- Route chat requests directly from Android to OpenRouter.
- Keep a small extensible tool layer for future Android actions and MCP server support.

## Implemented v1 Skeleton

- Onboarding screen for explaining OpenRouter costs and saving the API key locally.
- Settings screen for changing the API key and selected OpenRouter model.
- Chat screen with conversation history, loading state, and error display.
- OpenRouter repository using the Chat Completions endpoint.
- Tool interfaces plus initial device/app tools and confirmation-aware sensitive tool handling.
