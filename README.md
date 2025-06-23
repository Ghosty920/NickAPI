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
    implementation 'com.github.Ghosty920.NickAPI:Main:main-SNAPSHOT'
}

tasks.shadowJar {
    // Relocating prevents multiple plugins using the same folder and possibly breaking
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
    implementation("com.github.Ghosty920.NickAPI:Main:main-SNAPSHOT")
}

tasks.shadowJar {
    // Relocating prevents multiple plugins using the same folder and possibly breaking
    relocate("xyz.haoshoku.nick", "myproject.deps.xyz.haoshoku.nick")
}
```

**pom.xml:** (Use `mvn package`)

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.Ghosty920.NickAPI</groupId>
    <artifactId>Main</artifactId>
    <version>main-SNAPSHOT</version>
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
                <configuration>
                    <!-- Relocating prevents multiple plugins using the same folder and possibly breaking -->
                    <relocations>
                        <relocation>
                            <pattern>xyz.haoshoku.nick</pattern>
                            <shadedPattern>myproject.deps.xyz.haoshoku.nick</shadedPattern>
                        </relocation>
                    </relocations>
                </configuration>
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
	
	/**
     * Example implementation of a nick feature
	 */
	public void nick(Player player, String username) {
		NickAPI.nick(player, name);
		NickAPI.setSkin(player, name);
		NickAPI.setUniqueId(player, name);
		NickAPI.setGameProfileName(player, name);
		NickAPI.refreshPlayer(player);
	}

}
```

More methods can be found in
the [NickAPI class](https://github.com/Ghosty920/NickAPI/blob/main/API/src/main/java/xyz/haoshoku/nick/NickAPI.java).

## Building

Simply run `mvn clean package install -f pom.xml`, and it will automatically build and install it to your `.m2` folder.

Make sure to build using Java 21.

## Credits

Original Plugin/API (v6.7) by Haoshoku, under the MIT License.

[GitHub](https://github.com/Haoshoku/NickAPI/) - [SpigotMC](https://www.spigotmc.org/resources/26013/) - [Discord](https://haoshoku.xyz/go/discord) ([mirror](https://discord.gg/y9Vkm22VuZ))

Additional code by Ghosty, also under the MIT License.

[Website](https://ghosty.im/) - [Discord](https://ghosty.im/discord?from=nickapi)

All the support for the original plugin should be directed to Haoshoku. All the support for this library is on my
discord exclusively.