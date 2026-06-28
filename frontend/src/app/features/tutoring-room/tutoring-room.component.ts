import { Component, OnInit, OnDestroy, ElementRef, viewChild, effect, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-tutoring-room',
  standalone: true,
  imports: [],
  templateUrl: './tutoring-room.component.html'
})
export class TutoringRoomComponent implements OnInit, OnDestroy {
  zegoContainer = viewChild<ElementRef>('zegoContainer');

  private readonly APP_ID = 52259796;
  private readonly SERVER_SECRET = '0bdbadc00eaebeb3afaab431275e019c';

  roomID: string = 'global-room';
  userID: string = '';
  userName: string = 'Student';

  private zegoInstance: any = null;
  private authSub?: Subscription;
  private routeSub?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {
    effect(() => {
      if (isPlatformBrowser(this.platformId) && this.zegoContainer()) {
        this.joinRoom();
      }
    });
  }

  ngOnInit() {
    this.routeSub = this.route.params.subscribe(params => {
      if (params['id']) {
        this.roomID = params['id'];
      }
    });

    this.authSub = this.authService.currentUser$.subscribe((user: any) => {
      if (user) {
        this.userID = String(user.userId || this.generateUserID());
        this.userName = `${user.firstName} ${user.lastName}`;
      } else {
        this.userID = this.generateUserID();
        this.userName = 'Guest';
      }
    });
  }

  ngOnDestroy() {
    if (this.zegoInstance) {
      this.zegoInstance.destroy();
    }
    this.authSub?.unsubscribe();
    this.routeSub?.unsubscribe();
  }

  private async joinRoom(): Promise<void> {
    if (!this.zegoContainer()) return;

    try {
      const { ZegoUIKitPrebuilt } = await import('@zegocloud/zego-uikit-prebuilt');

      const kitToken = ZegoUIKitPrebuilt.generateKitTokenForTest(
        this.APP_ID,
        this.SERVER_SECRET,
        this.roomID,
        this.userID,
        this.userName
      );

      this.zegoInstance = ZegoUIKitPrebuilt.create(kitToken);

      this.zegoInstance.joinRoom({
        container: this.zegoContainer()!.nativeElement,
        scenario: {
          mode: ZegoUIKitPrebuilt.VideoConference
        },
        showPreJoinView: true,
        turnOnMicrophoneWhenJoining: false,
        turnOnCameraWhenJoining: true,
        showRoomTimer: true,
        showUserList: true,
        showTextChat: true
      });
    } catch (err) {
      console.error('[ZegoCloud] Failed to initialize tutoring room:', err);
    }
  }

  private generateUserID(): string {
    return 'user_' + Math.random().toString(36).substring(2, 10);
  }
}
