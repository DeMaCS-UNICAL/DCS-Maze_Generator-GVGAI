package it.unical.mat.asp_classes;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import it.unical.mat.embasp.languages.IllegalAnnotationException;
import it.unical.mat.embasp.languages.ObjectNotValidException;
import it.unical.mat.map_generator.MapGenerator;

public class RoomBuilder extends Thread {
	private MapGenerator mapGenerator;
	private Partition partition;
	private CyclicBarrier barrier;
	
	public RoomBuilder( MapGenerator mapGenerator, Partition partition, CyclicBarrier barrier )
	{
		this.mapGenerator = mapGenerator;
		this.partition = partition;
		this.barrier = barrier;
	}
	
	@Override
	public void run() 
	{
		try {
			mapGenerator.buildWalls(partition);
			barrier.await();
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException
				| ObjectNotValidException | IllegalAnnotationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
