import { RouterTestingModule } from '@angular/router/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImmersiveHubComponent } from './immersive-hub.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ImmersiveHubComponent', () => {
  let component: ImmersiveHubComponent;
  let fixture: ComponentFixture<ImmersiveHubComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [ImmersiveHubComponent, RouterTestingModule],
    providers: [provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
})
    .compileComponents();
    
    fixture = TestBed.createComponent(ImmersiveHubComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
