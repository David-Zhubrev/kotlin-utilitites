# Kotlin-utilities
## A collection of simple libraries that provide commonly used functionality

### Installation:
#### Gradle:
Add maven repository:
```
repositories{
    maven("https://repo.repsy.io/mvn/davidzhubrev/public")
}
```
Then, add the library you need:
```
implementation("com.appdav.kotlin-utilities:$library:$version")
```
All libraries are distributed with sources jars and provide documentation for public methods.

### Featured libraries:
#### Zipper
Fast way to zip/unzip your files
```
implementation("com.appdav.kotlin-utilities:zipper:1.0")
```
##### Usage:
To zip your files:
```
Zipper.zip(myZipFile, getFilesToZip())
```
To unzip your files:
```
Zipper.unzip(myZipFile, getDestinationFolder())
```

#### OS type provider
Get current OS system type
```
implementation("com.appdav.kotlin-utilities:os-type-provider:1.0")
```
##### Usage:
```
val currentOs: OsType = OsTypeProvider.current
```
OsType is enumeration, which makes it possible to use in exhaustive when-expressions:
```
 when(OsTypeProvider.current){
        OsType.MAC -> TODO()
        OsType.WINDOWS -> TODO()
        OsType.LINUX -> TODO()
        OsType.OTHER -> TODO()
    }
```

#### Command
Fast command-line commands usage
```
implementation("com.appdav.kotlin-utilities:command:1.2")
```
##### Usage:
```
Command("java --version").run(myWorkingDir).printOutput()
```
This executes java --version via system command-line interpreter.
You can also pass List<String> instead of String in order to make it more clear (strings are just split by whitespace)
```
Command(listOf("java", "--version").run(myWorkingDir, TimeOut(10L, TimeUnit.Seconds)
```
You can also pass the TimeOut instance in order to apply time-out for the command

Library also provides extension for both String and List<String>:
```
val result = "java --version".runCommand(workingDir)
```
