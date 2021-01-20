package io.jenkins.infra

import groovy.mock.interceptor.StubFor
import static org.junit.Assert.*

class DockerConfigTest extends GroovyTestCase {

  static String sutImageName = "sut"

  void testConstructorDefaultConfig() {
    def infraConfigMock = new StubFor(InfraConfig.class)
    infraConfigMock.use {
      def sut = new DockerConfig(sutImageName,new InfraConfig([env: 'foo']))
      assertEquals(sutImageName, sut.imageName)
      assertEquals('Dockerfile', sut.dockerfile)
      assertEquals('jenkins-dockerhub', sut.credentials)
      assertEquals('master', sut.mainBranch)
      assertNotEquals('', sut.buildDate)
    }
  }

  void testConstructorNoInfra() {
    def sut = new DockerConfig(sutImageName,null)
    assertEquals(sutImageName, sut.imageName)
    assertEquals(null, sut.infraConfig)
  }

  void testConstructorWithConfig() {
    def sut = new DockerConfig(sutImageName,null,[dockerfile: 'build.Dockerfile'])
    assertEquals(sutImageName, sut.imageName)
    assertEquals(null, sut.infraConfig)
    assertEquals('build.Dockerfile', sut.dockerfile)

    assertEquals('jenkins-dockerhub', sut.credentials)
    assertEquals('master', sut.mainBranch)
    assertNotEquals('', sut.buildDate)
  }

  void testGetRegistryDefaultConfig() {
    def expectedRegistry = 'registry.stub.dummy'
    def infraConfigMock = new StubFor(InfraConfig.class)
    infraConfigMock.demand.with {
      getDockerRegistry{ return expectedRegistry }
    }

    infraConfigMock.use {
      def sut = new DockerConfig(sutImageName, new InfraConfig([env: 'foo']))
      def gotRegistry = sut.getRegistry()
      Assert.assertEquals(expectedRegistry, gotRegistry)
    }
  }

  void testGetRegistryFromCustomConfig() {
    def expectedRegistry = 'registry.custom.dummy'
    def infraConfigMock = new StubFor(InfraConfig.class)
    infraConfigMock.demand.with {
      getDockerRegistry{ return expectedRegistry }
    }

    infraConfigMock.use {
      def sut = new DockerConfig(sutImageName, new InfraConfig([env: 'foo']), [registry: expectedRegistry])
      def gotRegistry = sut.getRegistry()
      Assert.assertEquals(expectedRegistry, gotRegistry)
    }
  }

  void testGetRegistryNoConfigNoInfra() {
    def sut = new DockerConfig("sut", null, [registry: expectedRegistry])
    def gotRegistry = sut.getRegistry()
    Assert.assertEquals('noregistry', gotRegistry)
  }
}
