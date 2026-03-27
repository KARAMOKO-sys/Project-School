import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserResearch } from './user-research';

describe('UserResearch', () => {
  let component: UserResearch;
  let fixture: ComponentFixture<UserResearch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserResearch]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserResearch);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
