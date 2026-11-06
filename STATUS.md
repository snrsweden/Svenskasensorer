# Svenska Sensorer — development status

## Implemented in source
- Official ATAK 5.6 plugin template and toolbar pane from the prior v0.1 project.
- Seven symbol preview choices, six colors, adjustable bearing, mils conversion, and persisted chooser preferences.
- `SensorRecord`: validated WGS84 coordinate, stable UUID, JSON serialization, bearing normalization.
- `SensorStore`: offline persistent CRUD operations for records.

## NOT yet implemented or verified
- ATAK map tap placement, live map markers, marker selection/editing, map polygon and route tools.
- Synchronization/CoT sharing, build against a verified 5.6 SDK, signing, or device testing.
- Source artwork is approximate and not certified as official Swedish Armed Forces symbology.

This ZIP is **source code, not an APK**. It must not be represented as a working ATAK map plugin yet.
