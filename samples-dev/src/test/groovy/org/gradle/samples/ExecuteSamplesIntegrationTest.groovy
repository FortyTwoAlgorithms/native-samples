package org.gradle.samples

import org.gradle.internal.os.OperatingSystem
import org.gradle.samples.fixtures.Samples
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assume
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Requires
import spock.lang.Specification
import spock.lang.Unroll


class ExecuteSamplesIntegrationTest extends Specification {
    @Rule TemporaryFolder tmpDir = new TemporaryFolder()

    def "can build 'cpp/binary-dependencies'"() {
        given:
        def prebuilt = Samples.useSampleIn('cpp/simple-library', tmpDir.root)
        GradleRunner.create()
                .withProjectDir(prebuilt.sampleDir)
                .withArguments("publish")
                .build()
        def target = Samples.useSampleIn('cpp/prebuilt-binaries', tmpDir.root)
        expect:
        GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments("build")
                .build()
    }

    def "can build 'cpp/prebuilt-binaries'"() {
        given:
        def prebuilt = Samples.useSampleIn('cpp/simple-library', tmpDir.root)
        GradleRunner.create()
                .withProjectDir(prebuilt.sampleDir)
                .withArguments("assembleRelease", "assembleDebug")
                .build()
        def target = Samples.useSampleIn('cpp/prebuilt-binaries', tmpDir.root)
        expect:
        GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments("build")
                .build()
    }

    @Unroll
    def "can build C++ '#sample.name'"() {
        given:
        Assume.assumeFalse(sample.sampleName == "prebuilt-binaries")
        Assume.assumeFalse(sample.sampleName == "binary-dependencies")
        def target = Samples.useSampleIn(sample.name, tmpDir.root)

        expect:
        GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments("build")
                .build()
        where:
        sample << Samples.getSamples("cpp")
    }

    @Requires({ !OperatingSystem.current().isWindows() })
    def "can build 'swift/prebuilt-binaries'"() {
        given:
        def prebuilt = Samples.useSampleIn('swift/simple-library', tmpDir.root)
        GradleRunner.create()
                .withProjectDir(prebuilt.sampleDir)
                .withArguments("assembleRelease", "assembleDebug")
                .build()
        def target = Samples.useSampleIn('swift/prebuilt-binaries', tmpDir.root)
        expect:
        GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments("build")
                .build()
    }

    @Requires({ !OperatingSystem.current().isWindows() })
    @Unroll
    def "can build Swift '#sample.name'"() {
        given:
        Assume.assumeFalse(sample.sampleName == "prebuilt-binaries")
        def target = Samples.useSampleIn(sample.name, tmpDir.root)

        expect:
        GradleRunner.create()
                .withProjectDir(target.sampleDir)
                .withArguments("build", '-i')
                .build()
        where:
        sample << Samples.getSamples("swift")
    }
}