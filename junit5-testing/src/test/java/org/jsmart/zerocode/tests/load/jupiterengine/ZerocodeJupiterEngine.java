//package org.jsmart.zerocode.tests.load.jupiterengine;
//
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Optional;
//import org.junit.platform.engine.EngineDiscoveryRequest;
//import org.junit.platform.engine.ExecutionRequest;
//import org.junit.platform.engine.TestDescriptor;
//import org.junit.platform.engine.UniqueId;
//import org.junit.platform.engine.discovery.ClassSelector;
//import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;
//
//public class ZerocodeJupiterEngine extends HierarchicalTestEngine<CucumberExecutionContext> {
//
//    static final String ENGINE_ID = "zerocode-jupiter";
//
//
//    @Override
//    public String getId() {
//        return ENGINE_ID;
//    }
//
//    @Override
//    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
//        ClassSelector selector = discoveryRequest.getSelectorsByType(ClassSelector.class).get(0);
//        Class<?> clazz = selector.getJavaClass();
//
//        return new TestDescriptorCreator(uniqueId, runtimeOptions, runtime, methodResolver).createEngineDescriptorFor(cucumberFeatures);
//    }
//
//
//    @Override
//    public Optional<String> getGroupId() {
//        return Optional.empty();
//    }
//
//    @Override
//    public Optional<String> getArtifactId() {
//        return Optional.empty();
//    }
//
//    @Override
//    public Optional<String> getVersion() {
//        return Optional.empty();
//    }
//
//
//    @Override
//    protected CucumberExecutionContext createExecutionContext(ExecutionRequest executionRequest) {
//        return null;
//    }
//}
