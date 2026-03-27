import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseBanner } from './course-banner';

describe('CourseBanner', () => {
  let component: CourseBanner;
  let fixture: ComponentFixture<CourseBanner>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseBanner]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CourseBanner);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
