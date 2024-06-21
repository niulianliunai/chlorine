package com.chlorine.base.mvc.util;

import org.springframework.stereotype.Component;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Subgraph;
import java.util.HashMap;
import java.util.Map;

@Component
public class EntityManagerUtil {
    @PersistenceContext
    private EntityManager em;

    public <T> T lazyLoad(Class<T> t,Long id ,String...subgraphList) {
        EntityGraph graph = this.em.createEntityGraph(t);
        Subgraph subGraph = graph.addSubgraph(subgraphList[0]);
        for (int i = 1; i < subgraphList.length; i++) {
           subGraph = addSubgraph(subGraph, subgraphList[i]);
        }
        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.fetchgraph", graph);
        return em.find(t, id, props);
    }
    public Subgraph addSubgraph(Subgraph graph, String subgraph) {
        return graph.addSubgraph(subgraph);
    }
}
