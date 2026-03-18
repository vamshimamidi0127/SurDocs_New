# FileNet Library Placement

Place the proprietary FileNet WSI Java API jars in this folder before building:

- `Jace.jar`

If your environment requires additional FileNet jars beyond `Jace.jar`, add them here and keep the backend `pom.xml` aligned with the same location.

This shared root-level `lib/filenet` folder is the canonical runtime/build location for FileNet dependencies in the cleaned project structure.
