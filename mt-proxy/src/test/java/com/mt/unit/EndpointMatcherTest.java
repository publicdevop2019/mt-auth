package com.mt.unit;

//import com.mt.edgeproxy.infrastructure.EndpointFilter;


public class EndpointMatcherTest {

//    @Test
//    public void getSecurityProfileWhenMultipleSecurityProfileFound() {
//        Endpoint appBizEndpointCardRep = new Endpoint();
//        appBizEndpointCardRep.setPath("/api/profiles/100");
//        Endpoint appBizEndpointCardRep2 = new Endpoint();
//        appBizEndpointCardRep2.setPath("/api/profiles/**");
//        Optional<Endpoint> mostSpecificSecurityProfile = EndpointFilter.EndpointMatcher.getMostSpecificSecurityProfile(List.of(appBizEndpointCardRep, appBizEndpointCardRep2));
//        Assert.assertEquals("/api/profiles/100", mostSpecificSecurityProfile.get().getPath());
//    }
//
//    @Test
//    public void getSecurityProfileWhenOneFound() {
//        Endpoint appBizEndpointCardRep = new Endpoint();
//        appBizEndpointCardRep.setPath("/api/profiles/**/addresses");
//        Optional<Endpoint> mostSpecificSecurityProfile = EndpointFilter.EndpointMatcher.getMostSpecificSecurityProfile(List.of(appBizEndpointCardRep));
//        Assert.assertEquals("/api/profiles/**/addresses", mostSpecificSecurityProfile.get().getPath());
//    }
//
//    @Test
//    public void getSecurityProfileWhenTwoFound() {
//        Endpoint appBizEndpointCardRep = new Endpoint();
//        appBizEndpointCardRep.setPath("/api/profiles/**/addresses");
//        Endpoint appBizEndpointCardRep2 = new Endpoint();
//        appBizEndpointCardRep2.setPath("/api/profiles/**/addresses/**");
//        Optional<Endpoint> mostSpecificSecurityProfile = EndpointFilter.EndpointMatcher.getMostSpecificSecurityProfile(List.of(appBizEndpointCardRep, appBizEndpointCardRep2));
//        Assert.assertEquals("/api/profiles/**/addresses", mostSpecificSecurityProfile.get().getPath());
//    }
}
