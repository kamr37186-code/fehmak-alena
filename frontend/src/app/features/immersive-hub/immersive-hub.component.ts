import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImmersiveHubService } from '../../core/services/immersive-hub.service';
import { ImmersiveSession, Peer } from '../../core/models/immersive-hub.model';

interface TranscriptItem {
  time: string;
  text: string;
}

@Component({
  selector: 'app-immersive-hub',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './immersive-hub.component.html',
  styleUrl: './immersive-hub.component.css'
})
export class ImmersiveHubComponent implements OnInit, OnDestroy {
  session: ImmersiveSession | null = null;
  peers: Peer[] = [];
  
  isSearchingPeers = false;
  isPlayingTranscript = false;
  currentTranscriptIndex = -1;
  transcriptTimer: any;

  transcript: TranscriptItem[] = [
    { time: '00:12', text: 'The human heart is the primary organ of the circulatory system.' },
    { time: '01:05', text: 'Notice how the left ventricle walls are significantly thicker than the right.' },
    { time: '01:45', text: 'This is because it must pump blood through the entire systemic circuit.' },
    { time: '02:20', text: 'Let\'s zoom into the mitral valve for a closer look at structural integrity.' }
  ];

  takeaways = [
    'Ventricle pressure mechanics',
    'Mitral valve morphology',
    'Systemic vs Pulmonary circuits'
  ];

  constructor(private hubService: ImmersiveHubService) {}

  ngOnInit(): void {
    this.hubService.getSession().subscribe(sess => {
      this.session = sess;
      this.currentTranscriptIndex = sess.currentTranscriptIndex;
    });

    // Initial dummy peers
    this.peers = [
      { name: 'Seif', avatarUrl: 'https://ui-avatars.com/api/?name=Seif&background=random', level: 'Lvl 32 Expert' },
      { name: 'Amr', avatarUrl: 'https://ui-avatars.com/api/?name=Amr&background=random', level: 'Lvl 28 Expert' }
    ];
  }

  ngOnDestroy(): void {
    this.pauseTranscript();
  }

  toggleTranscript() {
    if (this.isPlayingTranscript) {
      this.pauseTranscript();
    } else {
      this.playTranscript();
    }
  }

  playTranscript() {
    this.isPlayingTranscript = true;
    if (this.currentTranscriptIndex >= this.transcript.length - 1) {
      this.currentTranscriptIndex = 0;
    }
    
    this.transcriptTimer = setInterval(() => {
      if (this.currentTranscriptIndex < this.transcript.length - 1) {
        this.currentTranscriptIndex++;
      } else {
        this.pauseTranscript();
      }
    }, 3000); // Highlight next line every 3 seconds
  }

  pauseTranscript() {
    this.isPlayingTranscript = false;
    if (this.transcriptTimer) {
      clearInterval(this.transcriptTimer);
    }
  }

  generateNote(takeaway: string) {
    this.hubService.generateNote().subscribe(sess => {
      this.session = sess;
    });
  }

  searchPeers() {
    this.isSearchingPeers = true;
    this.peers = [];
    
    // Simulate delay
    setTimeout(() => {
      this.hubService.matchPeers().subscribe(matched => {
        this.peers = matched;
        this.isSearchingPeers = false;
      });
    }, 1500);
  }
}
