# HandyHood

A production-focused Android application for managing local service requests with real-time synchronization. Built on Jetpack Compose and Supabase, HandyHood prioritizes backend-enforced business rules and clean architecture over UI-only state management.

**Current Status**: Core user request flow complete with real-time updates. Provider features in active development.

## Key Features

- **Authenticated Request Management**: Create, edit, and cancel service requests with full lifecycle tracking
- **Real-time Synchronization**: Automatic UI updates via Supabase Realtime with debounced refresh logic
- **Backend-Enforced Rules**: Database-level Row Level Security (RLS) ensures data integrity independent of client state
- **Lifecycle State Machine**: Requests follow controlled state transitions (pending → accepted → completed) with immutability rules
- **Session Persistence**: Authentication state survives app restarts and process death

## Prerequisites

- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: 17 or higher (required for Kotlin 1.9+)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Supabase Project**: Active project with Auth and Realtime enabled

**Critical Dependencies**:
```gradle
- Kotlin: 1.9.0+
- Compose BOM: 2024.02.00+
- Supabase Kotlin SDK: Latest stable
```

## Installation

### 1. Clone and Open Project

```bash
git clone https://github.com/yourusername/handyhood.git
cd handyhood
```

Open in Android Studio. The IDE will trigger Gradle sync automatically.

### 2. Configure Supabase Backend

Create a `local.properties` file in the project root (this file is gitignored):

```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-key-here
```

**Important**: Never commit `SUPABASE_ANON_KEY` to version control. The anon key is intended for client-side use but should still be treated as sensitive.

### 3. Database Schema Setup

Execute this SQL in your Supabase SQL editor:

```sql
-- Create requests table
CREATE TABLE requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    category TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    preferred_date TIMESTAMPTZ,
    status TEXT DEFAULT 'pending' CHECK (status IN ('pending', 'accepted', 'completed', 'cancelled')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Enable RLS
ALTER TABLE requests ENABLE ROW LEVEL SECURITY;

-- Users can read their own requests
CREATE POLICY "Users can read own requests"
    ON requests FOR SELECT
    USING (auth.uid() = user_id);

-- Users can insert their own requests
CREATE POLICY "Users can insert own requests"
    ON requests FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- Users can update only pending requests they own
CREATE POLICY "Users can update own pending requests"
    ON requests FOR UPDATE
    USING (auth.uid() = user_id AND status = 'pending')
    WITH CHECK (auth.uid() = user_id AND status IN ('pending', 'cancelled'));

-- Enable Realtime
ALTER PUBLICATION supabase_realtime ADD TABLE requests;
```

**Why these policies matter**: The RLS policies enforce that completed or accepted requests are immutable from the client side. This prevents race conditions and ensures request lifecycle integrity even if the app is compromised.

### 4. Enable Realtime in Supabase Dashboard

Navigate to: Database → Replication → Enable replication for `requests` table

### 5. Build and Run

```bash
./gradlew assembleDebug
```

Or use Android Studio's run button. The app requires a physical device or emulator running API 24+.

**Common Gotcha**: If Gradle sync fails with "Unable to resolve dependency", clear Gradle cache:
```bash
./gradlew clean --refresh-dependencies
```

## Quick Start

### Create Your First Request

1. Register a new account via the auth screen (email + password)
2. Navigate to "Create Request"
3. Fill in:
    - Category: "Plumbing" (or any service type)
    - Title: "Fix leaking kitchen faucet"
    - Description: Optional details
    - Preferred Date: Select from date picker
4. Submit

The request appears immediately in your list with status "pending". Open the app on a second device (same account) and observe the real-time update.

### Test Real-time Sync

With the app open, manually update a request status in Supabase SQL editor:

```sql
UPDATE requests 
SET status = 'accepted' 
WHERE id = 'your-request-id';
```

The app UI updates within 2-3 seconds without requiring manual refresh.

## Usage

### Request Lifecycle

```
pending → accepted → completed
   ↓
cancelled (terminal)
```

- **pending**: User can edit or cancel
- **accepted**: Read-only (provider has claimed the request)
- **completed**: Read-only (work finished)
- **cancelled**: Terminal state (soft delete)

**Design Decision**: Hard deletes are disabled to maintain audit trail. Cancelled requests remain in the database but are marked inactive.

### Editing Requests

```kotlin
// Only pending requests show enabled edit buttons
if (request.status == "pending") {
    // Edit is allowed
} else {
    // UI shows disabled state with explanatory text
}
```

The app disables edit controls client-side, but the database RLS policies are the actual enforcement mechanism. This protects against modified APKs or API manipulation.

### Realtime Updates

The app uses a **debounced refresh strategy**:

- Realtime events trigger a refresh request
- Multiple rapid events are coalesced into a single refresh
- Debounce window: 300ms
- Loading indicator appears only if refresh takes >500ms

**Why debouncing**: Without it, rapid backend updates (e.g., bulk status changes) can cause UI thrashing and unnecessary network requests.

## Configuration

### Environment Variables

| Variable | Purpose | Required | Example |
|----------|---------|----------|---------|
| `SUPABASE_URL` | Supabase project URL | Yes | `https://abc.supabase.co` |
| `SUPABASE_ANON_KEY` | Public anon key | Yes | `eyJhbGc...` |

### Customization Options

**Request Categories**: Modify the category list in `CreateRequestScreen.kt`:

```kotlin
val categories = listOf(
    "Plumbing", "Electrical", "Carpentry", 
    "Cleaning", "Moving", "Other"
)
```

**Realtime Debounce Timing**: Adjust in `RequestsViewModel.kt`:

```kotlin
private val debounceDelay = 300L // milliseconds
```

**Session Timeout**: Controlled by Supabase Auth settings (default: 1 hour). Configure in Supabase Dashboard → Authentication → Settings.

## Architecture / Design Decisions

### Why Backend-Enforced Rules?

Android apps can be decompiled and modified. By enforcing business logic in Postgres RLS policies, we ensure data integrity regardless of client tampering. The app UI reflects these rules, but they're not the source of truth.

### Repository Pattern

```
UI (Compose) → ViewModel (StateFlow) → Repository (Supabase) → Backend
```

- **ViewModels** expose UI state as `StateFlow`, never raw mutable state
- **Repository** is the single source of truth for data operations
- **No direct Supabase calls from UI layer** (testability, separation of concerns)

### Real-time vs Polling

We chose Supabase Realtime over polling because:
- Lower latency (WebSocket vs HTTP)
- Reduced server load (no unnecessary requests)
- Better user experience (instant updates)

**Tradeoff**: Realtime requires stable WebSocket connection. On poor networks, fallback to manual refresh is available via pull-to-refresh gesture.

### StateFlow over LiveData

`StateFlow` provides:
- Kotlin coroutine integration
- Better testability (no Android framework dependencies)
- Type safety and null safety

## Testing

### Run Unit Tests

```bash
./gradlew test
```

### Run Instrumentation Tests

```bash
./gradlew connectedAndroidTest
```

**Test Coverage**: Currently focusing on ViewModel and Repository layers. UI tests are minimal given Compose's preview-driven development workflow.

**Known Gap**: Real-time synchronization logic needs integration tests against a test Supabase instance. Planned for next sprint.

## Contributing

### Code Style

- Follow official Kotlin coding conventions
- Use Android Studio's built-in formatter (Ctrl+Alt+L / Cmd+Option+L)
- Compose: Prefer remember + derivedStateOf over unnecessary recomposition

### Pull Request Process

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/provider-dashboard`)
3. Commit with clear messages describing **why**, not just what
4. Ensure tests pass locally
5. Submit PR with description of changes and rationale

### Commit Message Format

```
[Component] Brief description

Detailed explanation of changes and reasoning.
Mention any breaking changes or migration steps.
```

Example:
```
[Auth] Add biometric authentication support

Implements fingerprint/face unlock as alternative to password entry.
Requires Android 9+ (BiometricPrompt API).
Backward compatible - falls back to password on older devices.
```

## Troubleshooting

### "Supabase connection failed" on app launch

**Cause**: Invalid or missing Supabase credentials in `local.properties`.

**Solution**:
1. Verify `SUPABASE_URL` and `SUPABASE_ANON_KEY` are set
2. Check for trailing spaces or newlines in values
3. Rebuild project: `./gradlew clean build`

### Realtime updates not appearing

**Cause**: Realtime replication not enabled for `requests` table.

**Solution**:
1. Go to Supabase Dashboard → Database → Replication
2. Enable replication for `requests` table
3. Restart app

**Alternative cause**: WebSocket connection blocked by firewall/proxy. Test on cellular data to confirm.

### "Permission denied" when updating request

**Cause**: Attempting to edit a non-pending request, or RLS policies not applied correctly.

**Solution**:
- Verify request status is `pending` in Supabase table viewer
- Re-run RLS policy SQL from Installation section
- Check Supabase logs for policy violations

### Gradle sync fails with dependency resolution error

**Cause**: Stale Gradle cache or incompatible dependency versions.

**Solution**:
```bash
./gradlew clean
./gradlew --refresh-dependencies
# If still failing:
rm -rf ~/.gradle/caches
```

### App crashes on older Android versions

**Cause**: Using API 24 as minSdk means no Java 8 desugaring issues, but some Material 3 components require API 28+ for full functionality.

**Workaround**: Material 3 components gracefully degrade on older APIs. If crashes occur, check stack trace for specific component and consider alternative from Material 2.

## Roadmap

### In Progress
- Provider role and request acceptance workflow
- Push notification support via Firebase Cloud Messaging
- Payment integration (considering Stripe)

### Planned
- Admin dashboard for request analytics
- Rating and review system
- Multi-language support (i18n)

### Nice to Have
- Offline-first architecture with local SQLite cache
- Request geolocation and map view
- Provider background checks integration

## License

MIT License - see [LICENSE](LICENSE) file for details.

**Note**: Supabase itself has its own licensing terms for hosted services. This license covers only the HandyHood application code.

---

## Questions or Issues?

Open an issue on GitHub with:
- Device model and Android version
- Steps to reproduce
- Relevant logs (use `adb logcat` filtered by app package name)

**Response time**: Best effort, typically within 48 hours for critical bugs.

---

**Built with pragmatism over perfection. Ship early, iterate based on real usage.**