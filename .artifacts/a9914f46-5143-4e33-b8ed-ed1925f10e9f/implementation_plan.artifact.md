# Implementation Plan - Fix `onToggleTracking` Functionality

The `onToggleTracking` functionality in `MainScreen.kt` is not working as expected because the `isTracking` state is never updated in the `RunningRepository`, which is the source of truth for the UI state. Additionally, there are some minor inconsistencies in the service action strings and intent handling.

## User Review Required

> [!IMPORTANT]
> The `RunningRepository` was missing state updates for the `isTracking` flag. I will add these updates to ensure the UI reflects the current tracking status.

## Proposed Changes

### Core Data Layer

#### [MODIFY] [RunningRepository.kt](file:///F:/Android/AndroidApps/Projects/Run/app/src/main/java/com/varram/run/data/repository/RunningRepository.kt)
- Update `_runningState` in `startRun()` to set `isTracking = true`.
- Update `_runningState` in `finishRun()` to set `isTracking = false`.

### Service Layer

#### [MODIFY] [LocationTrackingService.kt](file:///F:/Android/AndroidApps/Projects/Run/app/src/main/java/com/varram/run/service/LocationTrackingService.kt)
- Standardize the action strings to use the actual package name `com.varram.run`.
- Ensure `stopTracking()` correctly updates the repository state before the service is destroyed.

### Presentation Layer

#### [MODIFY] [RunningTrackerViewModel.kt](file:///F:/Android/AndroidApps/Projects/Run/app/src/main/java/com/varram/run/feature/home/presentation/HomeViewModel.kt)
- Update the action constants if they were changed in the service (though they are referenced from the service class, so it might not be strictly necessary, but good to check).

## Verification Plan

### Manual Verification
1.  Launch the app.
2.  Click "[ START RUN ]" button.
3.  Verify the button text changes to "[ STOP RUN ]".
4.  Verify the notification appears.
5.  Click "[ STOP RUN ]" button.
6.  Verify the button text changes back to "[ START RUN ]".
7.  Verify the notification disappears.
