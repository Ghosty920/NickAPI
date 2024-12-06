# NickAPI

NickAPI is a library that allows disguising any player with a given nickname.

With this fork, the API can be directly shaded into your plugin, removing the need to add a third-party plugin.

## Usage

You can include the plugin using [JitPack](https://jitpack.io/#Ghosty920/NickAPI).

**build.gradle:** (Use `gradlew shadowJar`)

```groovy
plugins {
    id 'com.gradleup.shadow' version '8.3.5'
}

repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Ghosty920:NickAPI:master-SNAPSHOT'
}

tasks.shadowJar {
    relocate 'xyz.haoshoku.nick', 'myproject.deps.xyz.haoshoku.nick'
}
```

**build.gradle.kts:** (Use `gradlew shadowJar`)

```kts
plugins {
    id("com.gradleup.shadow") version "8.3.5"
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Ghosty920:NickAPI:master-SNAPSHOT")
}

tasks.shadowJar {
    relocate("xyz.haoshoku.nick", "myproject.deps.xyz.haoshoku.nick")
}
```

**pom.xml:**

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.Ghosty920</groupId>
    <artifactId>NickAPI</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
</dependencies>

<build>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-shade-plugin</artifactId>
        <version>3.5.3</version>
        <executions>
            <execution>
                <phase>package</phase>
                <goals>
                    <goal>shade</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
</plugins>
</build>
```

Use `NickAPI#setPlugin(Plugin)` to enable the API, and `NickAPI#onDisable()` to disable it.

```java
public class MyPlugin extends JavaPlugin {
	
	@Override
	public void onEnable() {
		// this part is optional, set to use a custom config
		NickAPI.setConfig(File | ConfigurationSection);
		
		NickAPI.setPlugin(this);
	}
	
	@Override
	public void onDisable() {
		NickAPI.onDisable();
	}

}
```

All methods can be found in
the [NickAPI class](https://github.com/Ghosty920/NickAPI/blob/master/API/src/main/java/xyz/haoshoku/nick/NickAPI.java).

## Building

1. Use [BuildTools](https://www.spigotmc.org/wiki/buildtools/) to make a build of all Spigot versions you need. (in this
   case, 1.21.3, 1.21.1, 1.20.6, 1.20.4, 1.20.2, and 1.20.1).
2. Run `mvn clean install`

## Credits

Original Plugin/API by Haoshoku, under the MIT License.

[GitHub](https://github.com/Haoshoku/NickAPI/)

[SpigotMC](https://www.spigotmc.org/resources/26013/)

[Discord](https://haoshoku.xyz/go/discord) ([mirror](https://discord.gg/y9Vkm22VuZ))