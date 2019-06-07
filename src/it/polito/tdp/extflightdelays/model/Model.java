package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	SimpleWeightedGraph<Airport, DefaultWeightedEdge> grafo;
	Map<Integer, Airport> aIdMap;
	Map<Airport, Airport> visite;	//mappa che modella la relazione padre - figlio
	ExtFlightDelaysDAO dao;
	
	public Model() {
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.aIdMap = new HashMap<Integer, Airport>();
		visite = new HashMap<Airport, Airport>();
		dao = new ExtFlightDelaysDAO ();
	}
	
	public void creaGrafo(int distanzaMedia) {
		
		dao.loadAllAirports(aIdMap);	//popolo la mappa
		
		// Aggiungo i vertici
		Graphs.addAllVertices(grafo,  aIdMap.values());
	
		// Aggiungo gli archi
		for(Rotta rotta: dao.getRotte(aIdMap, distanzaMedia)) {
			// Controllare se esiste già un arco tra i due vertici
			// se esiste, aggiorno il peso
			// Questo perchè devo considerare i voli da A a B e da B a A
			
			DefaultWeightedEdge edge = grafo.getEdge(rotta.getSource(), rotta.getDestination());
			if(edge == null) {
				Graphs.addEdge(grafo, rotta.getSource(), rotta.getDestination(), rotta.getDistanzaMedia());
			}
			else {
				double peso = grafo.getEdgeWeight(edge);
				double newPeso = ( peso + rotta.getDistanzaMedia())/2;
				System.out.println("Aggiornare peso vecchio: "+peso+ " con peso nuovo: "+newPeso);
				grafo.setEdgeWeight(edge, newPeso);
			}
			
		}

		System.out.println("Grafo creato!");
		System.out.println("Vertici " + grafo.vertexSet().size());
		System.out.println("Archi " + grafo.edgeSet().size());
	}
	
	public boolean testConnessione(Integer a1, Integer a2) {
		Set<Airport> visitati = new HashSet<Airport>();
		Airport partenza = aIdMap.get(a1);
		Airport destinazione = aIdMap.get(a2);
		System.out.println("Testo connessione tra "+ partenza +" e "+ destinazione);
		
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<Airport, DefaultWeightedEdge>(this.grafo, partenza);
		//iteratore per la visita in ampiezza
		
		while(it.hasNext()) {
			visitati.add(it.next());
		}
		
		if(visitati.contains(destinazione))
			return true;
		else 
			return false;
	}
	
	//STAMPARE UN POSSIBILE PERCORSO (SE ESISTE) TRA I DUE AEROPORTI
	//Agganciare un Listener all'iteratore di visita, cioè un metodo che viene eseguito
	// ogni volta che la visita attraversa ad esempio un arco
	// nel listener salvo l'informazione sulla visita, per poi ricostruire il percorso
	
	public List<Airport> trovaPercorso(Integer a1, Integer a2){
		List<Airport> percorso = new ArrayList<Airport>();
		Airport partenza = aIdMap.get(a1);
		Airport destinazione = aIdMap.get(a2);
		System.out.println("Cerco connessione tra "+ partenza +" e "+ destinazione);
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<Airport, DefaultWeightedEdge>(this.grafo, partenza);
		
		// Inserisco il nodo radice
		visite.put(partenza, null);		//mappa che modella la relazione padre - figlio
		
								//definisco classe anonima
		it.addTraversalListener(new TraversalListener<Airport, DefaultWeightedEdge>(){

			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void edgeTraversed(EdgeTraversalEvent<DefaultWeightedEdge> ev) {
				//Dal grafo recupero il vertice sorgente e destinazione dell'arco attraversato (passato come parametro)
				Airport sorgente = grafo.getEdgeSource(ev.getEdge());
				Airport destinazione = grafo.getEdgeTarget(ev.getEdge());
				
				// Popolo la mappa visite
				if(!visite.containsKey(destinazione) && visite.containsKey(sorgente)) {
					visite.put(destinazione, sorgente);
				}	else if (visite.containsKey(destinazione) && !visite.containsKey(sorgente)) {
					visite.put(sorgente, destinazione);
				}
			}

			@Override
			public void vertexFinished(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void vertexTraversed(VertexTraversalEvent<Airport> arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		while(it.hasNext())
			it.next();
		
		//Aeroporti non sono collegati
		if(!visite.containsKey(partenza) || !visite.containsKey(destinazione))
			return null;
		
		//Parto dal nodo destinazione e risalgo all'indietro fino alla partenza
		Airport step = destinazione;
		while (!step.equals(partenza)) {  //finchè non raggiungo la partenza
			percorso.add(step);	// aggiungo alla lista di aeroporti, l'aeroporto in cui sono
			step = visite.get(step);	// ricalcolo lo step per risalire la mappa
		}
		percorso.add(step); //Per raggiungere il nodo sorgente
		return percorso;
	}

	public List<Airport> getAeroporti() {
		return dao.loadAllAirports(aIdMap);
	}
	
}
