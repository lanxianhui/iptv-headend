package pl.lodz.p.cm.ctp.npvrd;

public class ScheduleUpdater implements Runnable {
	
	private ChannelRecorder channel;

	public ScheduleUpdater(ChannelRecorder channel) {
		this.channel = channel;
	}
	
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			System.out.println(channel.getGroupIp());
		}
	}

}
