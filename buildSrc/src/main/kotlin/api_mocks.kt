import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.api.artifacts.ModuleIdentifier
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.MutableVersionConstraint
import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.artifacts.capability.CapabilitySelector
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.capabilities.Capability
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.FileVisitor
import org.gradle.api.internal.artifacts.capability.SpecificCapabilitySelector
import org.gradle.api.internal.artifacts.dependencies.AbstractModuleDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultMutableVersionConstraint
import org.gradle.api.internal.artifacts.dependencies.SelfResolvingDependencyInternal
import org.gradle.api.internal.file.AbstractFileTree
import org.gradle.api.internal.file.FileCollectionStructureVisitor
import org.gradle.api.internal.file.FileTreeInternal
import org.gradle.api.internal.file.FileTreeInternal.DEFAULT_TREE_DISPLAY_NAME
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.util.PatternFilterable
import java.io.File
import java.util.function.Consumer

const val GROUP = "me.owdding.meowdding-lib"
const val MODULE = "meowdding-lib"

data class ComponentDependency(
    val task: TaskProvider<out Task>,
    val projectVersion: String,
    val componentName: String,
) : FileCollectionDependency, SelfResolvingDependencyInternal, ModuleComponentIdentifier, ModuleIdentifier {
    override fun getFiles(): FileCollection = task.map { it.outputs.files }.get()
    override fun getGroup(): String = GROUP
    override fun getModule(): String = "${MODULE}-$componentName"

    override fun getName(): String = "${MODULE}-$componentName"
    override fun getVersion(): String = projectVersion
    override fun getModuleIdentifier(): ModuleIdentifier = this
    override fun copy(): Dependency = this

    override fun getReason(): String? = null
    override fun because(reason: String?) {}
    override fun getTargetComponentId(): ComponentIdentifier = this
    override fun getDisplayName(): String = "Meowdding Lib Component $name"
}

data class FakeCapability(val componentName: String, val mcVersion: String) : Capability {
    override fun getGroup(): String = GROUP
    override fun getName(): String = "$MODULE-${componentName.lowercase()}-$mcVersion"
    override fun getVersion(): String = version.toString()
}

object FakeIdentifier : ModuleIdentifier, ModuleComponentIdentifier {
    override fun getGroup(): String = GROUP
    override fun getModule(): String = MODULE

    override fun getVersion(): String = version.toString()

    override fun getModuleIdentifier(): ModuleIdentifier = this
    override fun getName(): String = MODULE
    override fun getDisplayName(): String = "Meowddung Lib"
}

data class FakeDependency(
    val componentName: String,
    val projectVersion: String,
    val mcVersion: String,
) : FileCollectionDependency, SelfResolvingDependencyInternal, ExternalDependency, ExternalModuleDependency, AbstractModuleDependency() {
    override fun getGroup(): String = GROUP
    override fun getModule(): ModuleIdentifier = FakeIdentifier

    override fun getName(): String = "$MODULE-${componentName.lowercase()}"
    override fun getVersion(): String = projectVersion
    override fun matchesStrictly(identifier: ModuleVersionIdentifier): Boolean = false

    override fun isForce(): Boolean = false
    override fun isChanging(): Boolean = false

    override fun setChanging(changing: Boolean): ExternalModuleDependency = this

    override fun copy(): ExternalModuleDependency = this.copy(componentName = componentName)
    override fun version(configureAction: Action<in MutableVersionConstraint>) {}

    override fun getVersionConstraint(): VersionConstraint = DefaultMutableVersionConstraint.withVersion(projectVersion)

    override fun getRequestedCapabilities(): List<Capability> = listOf(FakeCapability(componentName, mcVersion))
    override fun getCapabilitySelectors(): Set<CapabilitySelector> = getRequestedCapabilities().map {
        object : SpecificCapabilitySelector {
            override fun getGroup(): String = it.group
            override fun getName(): String = it.name
            override fun getDisplayName(): String = ""
        }
    }.toSet()

    override fun getReason(): String? = null
    override fun because(reason: String?) {}
    override fun getTargetComponentId(): ComponentIdentifier = FakeIdentifier
    override fun getFiles(): FileCollection = EmptyFileTree
}

internal object EmptyFileTree : AbstractFileTree() {
    override fun getDisplayName(): String = DEFAULT_TREE_DISPLAY_NAME
    override fun getFiles(): Set<File> = setOf()
    override fun isEmpty(): Boolean = true
    override fun matching(filterConfigClosure: Closure<*>): FileTree = this
    override fun matching(filterConfigAction: Action<in PatternFilterable>): FileTree = this
    override fun visitContentsAsFileTrees(visitor: Consumer<FileTreeInternal>) {}
    override fun matching(patterns: PatternFilterable): FileTreeInternal = this
    override fun visit(visitor: FileVisitor): FileTree = this
    override fun visitContents(visitor: FileCollectionStructureVisitor) {}
}
